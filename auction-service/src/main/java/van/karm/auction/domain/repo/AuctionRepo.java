package van.karm.auction.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.auction.domain.model.Auction;
import van.karm.auction.domain.repo.projection.AuctionAndOwnerIds;
import van.karm.auction.domain.repo.projection.UserEnableAndBockMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface AuctionRepo extends JpaRepository<Auction, UUID> {

    @Query(value = """
SELECT CASE
         WHEN CURRENT_TIMESTAMP < a.start_date THEN 'The auction has not started'
         WHEN CURRENT_TIMESTAMP >= a.end_date THEN 'Auction has ended'
         WHEN a.is_private AND NOT EXISTS (
             SELECT 1 FROM auction_allowed_users au
             WHERE au.auction_id = a.id AND au.user_id = :userId
         ) THEN 'You have been denied access to this auction'
         WHEN :bidAmount < a.start_price THEN 'The bid must be equal to or higher than the start price'
         WHEN :bidAmount <= COALESCE(b.last_bid, 0) THEN 'The bid must be higher than the previous one'
         WHEN :bidAmount < COALESCE(b.last_bid, 0) + a.bid_increment THEN 'The bid does not match the minimum step'
       END AS error_message
FROM auction a
LEFT JOIN (
    SELECT auction_id, MAX(amount) AS last_bid
    FROM bid
    GROUP BY auction_id
) b ON b.auction_id = a.id
WHERE a.id = :auctionId
""", nativeQuery = true)
    String validateBid(
            @Param("auctionId") UUID auctionId,
            @Param("userId") UUID userId,
            @Param("bidAmount") BigDecimal bidAmount
    );

    @Query("SELECT a.ownerId FROM Auction a WHERE a.id = :auctionId")
    Optional<UUID> getOwnerIdById(UUID auctionId);


    @Modifying
    @Query(value = """
DELETE FROM auction a
WHERE a.id = :auctionId
  AND (
    NOT EXISTS (SELECT 1 FROM bid b WHERE b.auction_id = a.id)
    OR NOT EXISTS (SELECT 1 FROM bid b WHERE b.auction_id = a.id AND b.user_id <> a.owner_id)
  )
""", nativeQuery = true)
    int deleteAuctionByIdIfAllowed(@Param("auctionId") UUID auctionId);


    @Modifying
    @Query("UPDATE Auction a SET a.checked = true " +
            "WHERE a.checked = false " +
            "AND (a.endDate <= CURRENT_TIMESTAMP OR a.status = 'FINISHED') " +
            "AND a.status <> 'INACTIVE'")
    void markFinishedAsChecked();


    @Modifying
    @Query("UPDATE Auction a " +
            "SET a.status = 'INACTIVE', a.checked = true " +
            "WHERE a.id IN :ids")
    void updateStatusToInactive(@Param("ids") List<UUID> ids);


    @Modifying
    @Query(
            value = "INSERT INTO auction_allowed_users(auction_id, user_id) VALUES(:auctionId, :userId) ON CONFLICT DO NOTHING",
            nativeQuery = true
    )
    void addAllowedUser(@Param("auctionId") UUID auctionId, @Param("userId") UUID userId);


    @Query("SELECT COUNT(a) FROM Auction a JOIN a.allowedUserIds u WHERE a.id = :auctionId")
    int countAllowedUsersById(@Param("auctionId") UUID auctionId);

    @Modifying
    @Query(value = """
    UPDATE auction a
    SET status = 'FINISHED',
        winner_id = sub.last_user,
        final_amount = sub.last_amount
    FROM (
        SELECT DISTINCT ON (b.auction_id)
               b.auction_id,
               b.user_id AS last_user,
               b.amount AS last_amount
        FROM bid b
        ORDER BY b.auction_id, b.created_at DESC
    ) sub
    WHERE a.id = sub.auction_id
      AND a.status NOT IN ('FINISHED', 'INACTIVE')
      AND a.end_date <= CURRENT_TIMESTAMP
    """, nativeQuery = true)
    int finishExpiredAuctions();


    @Query("SELECT new van.karm.auction.domain.repo.projection.AuctionAndOwnerIds(a.id, a.ownerId) " +
            "FROM Auction a " +
            "WHERE (a.endDate <= CURRENT_TIMESTAMP OR a.status = 'FINISHED') " +
            "AND a.status <> 'INACTIVE'" +
            "AND a.checked = false")
    List<AuctionAndOwnerIds> getAllFinishedAuctions();

    @Query("""
    SELECT CASE 
             WHEN a.isPrivate = false THEN true
             WHEN :userId MEMBER OF a.allowedUserIds THEN true
             ELSE false 
           END
    FROM Auction a
    WHERE a.id = :auctionId
""")
    Optional<Boolean> findAllowed(@Param("auctionId") UUID auctionId,
                                  @Param("userId") UUID userId);


    @Query("SELECT a.id FROM Auction a WHERE a.status = 'INACTIVE'")
    Stream<UUID> streamInactiveIds();

    @Modifying
    @Query("DELETE FROM Auction a WHERE a.id IN :ids")
    void deleteByIds(@Param("ids") List<UUID> ids);

    @Query(
            value = """
        SELECT a.id
        FROM auction a
        LEFT JOIN users u ON a.owner_id = u.id
        WHERE a.status = 'ACTIVE' 
          AND (u.id IS NULL OR u.deleted = true)
        """,
            nativeQuery = true
    )
    Stream<UUID> streamActiveWithDeletedUser();


    @Modifying
    @Query(value = """
    WITH moved AS (
        DELETE FROM auction
        WHERE status = 'FINISHED'
        RETURNING *
    )
    INSERT INTO archived_auctions (
        id, title, description, start_price, bid_increment,
        reserve_price, is_private, status,
        start_date, end_date, owner_id, currency, winner_id, final_amount
    )
    SELECT
        id,
        title,
        description,
        start_price,
        bid_increment,
        reserve_price,
        is_private,
        status,
        start_date,
        end_date,
        owner_id,
        currency,
        winner_id,
        final_amount
    FROM moved
    RETURNING id
""", nativeQuery = true)
    List<UUID> moveFinishedAuctionsToArchive();

    int deleteAuctionById(UUID id);

    @Query(value = """
    SELECT enabled AS isEnable, block_reason AS blockMessage 
    FROM users 
    WHERE id = :id
""", nativeQuery = true)
    UserEnableAndBockMessage findUserEnableAndBockMessageById(@Param("id") UUID id);

    @Query(value = """
    SELECT EXISTS(
        SELECT 1
        FROM auction a
        WHERE a.id = :auctionId AND a.owner_id = :userId
    )
    """, nativeQuery = true)
    boolean isOwner(@Param("userId") UUID userId, @Param("auctionId") UUID auctionId);


    @Modifying
    @Query("UPDATE Auction a SET a.accessCodeHash = :codeHash WHERE a.id = :auctionId")
    int setNewCodeHash(@Param("codeHash") String codeHash, @Param("auctionId") UUID auctionId);

    @Modifying
    @Query(
            value = "DELETE FROM auction_allowed_users WHERE auction_id = :auctionId",
            nativeQuery = true
    )
    void deleteAllowedUsersByAuctionId(@Param("auctionId") UUID auctionId);

}
