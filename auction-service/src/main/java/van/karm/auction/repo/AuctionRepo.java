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
       (SELECT MAX(b.amount)\s
        FROM bid b\s
        WHERE b.auction_id = a.id) as lastBid
FROM auction a
WHERE a.id = :auctionId
""", nativeQuery = true)
    Optional<AuctionForBidProjection> findAuctionWithLastBid(UUID auctionId);
}
