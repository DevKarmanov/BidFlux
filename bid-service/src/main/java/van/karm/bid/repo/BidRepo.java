package van.karm.bid.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import van.karm.bid.model.Bid;
import van.karm.bid.repo.projection.BidAmountOnly;

import java.util.UUID;

public interface BidRepo extends JpaRepository<Bid, UUID> {
    BidAmountOnly findTopByAuctionIdOrderByCreatedAtDesc(UUID auctionId);
}
