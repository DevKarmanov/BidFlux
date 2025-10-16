package van.karm.bid.infrastructure.service.grpc.server;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import van.karm.bid.BidServiceGrpc;
import van.karm.bid.DecisionOnAuctionInactivity;
import van.karm.bid.PotentiallyInactiveAuctions;
import van.karm.bid.PotentiallyInactiveAuctionsList;
import van.karm.bid.domain.model.Bid;
import van.karm.bid.domain.repo.BidRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class BidGrpcServiceImpl extends BidServiceGrpc.BidServiceImplBase {
    private final BidRepo bidRepo;

    @Override
    public void checkAuctionsActivity(PotentiallyInactiveAuctionsList request, StreamObserver<DecisionOnAuctionInactivity> responseObserver) {
        List<PotentiallyInactiveAuctions> auctions = request.getAuctionsList();

        List<Bid> bids = bidRepo.findAllByAuctionIdIn(auctions.stream().map(o->UUID.fromString(o.getAuctionId())).toList());
        Map<String, Boolean> auctionStatus = new HashMap<>();

        for (PotentiallyInactiveAuctions auction : auctions) {
            List<Bid> auctionBids = bids.stream()
                    .filter(b -> b.getAuctionId().toString().equals(auction.getAuctionId()))
                    .toList();

            boolean inactive = auctionBids.isEmpty() || auctionBids.stream()
                    .allMatch(b -> b.getUserId().toString().equals(auction.getAuctionOwnerId()));

            auctionStatus.put(auction.getAuctionId(), inactive);
        }

        var response = DecisionOnAuctionInactivity.newBuilder()
                .putAllResults(auctionStatus)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
