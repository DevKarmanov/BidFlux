package van.karm.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import van.karm.auction.model.Auction;

import java.util.UUID;

public interface AuctionRepo extends JpaRepository<Auction, UUID> {
}
