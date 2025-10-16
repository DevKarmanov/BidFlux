package van.karm.auction.domain.repo.projection;

import java.util.UUID;

public record AuctionAndOwnerIds(UUID auctionId, UUID ownerId) {
}
