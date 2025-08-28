package van.karm.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import van.karm.auction.model.Auction;
import van.karm.auction.repo.projection.AuctionForBidProjection;

import java.util.Optional;
import java.util.UUID;

public interface AuctionRepo extends JpaRepository<Auction, UUID> {
    @Query(value = """
SELECT a.id as id,
       a.start_price as startPrice,
       a.bid_increment as bidIncrement,
       a.is_private as isPrivate,
       a.status as status,
       a.currency as currency,
       (SELECT MAX(b.amount)
        FROM bid b
        WHERE b.auction_id = a.id) as lastBid,
       COALESCE(array_agg(au.user_id), '{}') AS allowedUsers
FROM auction a
LEFT JOIN auction_allowed_users au ON au.auction_id = a.id
WHERE a.id = :auctionId
GROUP BY a.id, a.start_price, a.bid_increment, a.is_private, a.status, a.currency
""", nativeQuery = true)
    Optional<AuctionForBidProjection> findAuctionWithLastBid(UUID auctionId);
}
