package van.karm.auction.infrastructure.grpc.client.bid;

import van.karm.auction.domain.repo.projection.AuctionAndOwnerIds;

import java.util.List;
import java.util.Map;

public interface BidClient {
    Map<String,Boolean> checkInactive(List<AuctionAndOwnerIds> auctions);
}
