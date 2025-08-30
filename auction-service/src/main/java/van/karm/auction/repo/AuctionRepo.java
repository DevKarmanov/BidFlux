package van.karm.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.auction.model.Auction;

import java.math.BigDecimal;
import java.util.UUID;

public interface AuctionRepo extends JpaRepository<Auction, UUID> {

    @Query(value = """
SELECT CASE
         WHEN a.status <> 'ACTIVE' THEN 'Auction is not active'
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


    @Modifying
    @Query(
            value = "INSERT INTO auction_allowed_users(auction_id, user_id) VALUES(:auctionId, :userId) ON CONFLICT DO NOTHING",
            nativeQuery = true
    )
    void addAllowedUser(@Param("auctionId") UUID auctionId, @Param("userId") UUID userId);

}
