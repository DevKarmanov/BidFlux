package van.karm.bid.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import van.karm.bid.domain.model.Bid;

import java.util.UUID;

public interface BidRepo extends JpaRepository<Bid, UUID> {
}
