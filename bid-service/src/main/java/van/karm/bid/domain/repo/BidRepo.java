package van.karm.bid.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.bid.domain.model.Bid;
import van.karm.bid.domain.repo.projection.UserEnableAndBockMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BidRepo extends JpaRepository<Bid, UUID> {
    List<Bid> findAllByAuctionIdIn(List<UUID> auctionIds);
    @Query(value = """
    SELECT amount
    FROM bid
    WHERE auction_id = :auctionId
    ORDER BY created_at DESC
    LIMIT 1
""", nativeQuery = true)
    BigDecimal findLastBidAmount(@Param("auctionId") UUID auctionId);

    @Query(value = """
    SELECT enabled AS isEnable, block_reason AS blockMessage 
    FROM users 
    WHERE id = :id
""", nativeQuery = true)
    UserEnableAndBockMessage findUserEnableAndBockMessageById(@Param("id") UUID id);


    @Query(value = """
    SELECT CASE
        WHEN a.is_private = FALSE THEN TRUE
        ELSE EXISTS (
            SELECT 1
            FROM auction_allowed_users au
            WHERE au.auction_id = :auctionId
              AND au.user_id = :userId
        )
    END
    FROM auction a
    WHERE a.id = :auctionId
""", nativeQuery = true)
    boolean isUserAllowedForAuction(
            @Param("auctionId") UUID auctionId,
            @Param("userId") UUID userId
    );


}
