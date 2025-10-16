package van.karm.auction.infrastructure.grpc.client.bid;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import van.karm.auction.domain.repo.projection.AuctionAndOwnerIds;
import van.karm.auction.infrastructure.grpc.GrpcExceptionHandler;
import van.karm.bid.BidServiceGrpc;
import van.karm.bid.PotentiallyInactiveAuctions;
import van.karm.bid.PotentiallyInactiveAuctionsList;

import java.util.List;
import java.util.Map;

@Component
public class BidClientImpl implements BidClient {
    @GrpcClient("bid-service")
    private BidServiceGrpc.BidServiceBlockingStub bidServiceBlockingStub;
    private final static Logger log = LoggerFactory.getLogger(BidClientImpl.class);

    @Override
    public Map<String, Boolean> checkInactive(List<AuctionAndOwnerIds> auctions) {
        log.info("Checking {} auctions for inactivity via gRPC", auctions.size());

        try {
            List<PotentiallyInactiveAuctions> potentiallyInactiveAuctions =
                    auctions.stream()
                            .map(o -> PotentiallyInactiveAuctions.newBuilder()
                                    .setAuctionId(o.auctionId().toString())
                                    .setAuctionOwnerId(o.ownerId().toString())
                                    .build())
                            .toList();

            var list = PotentiallyInactiveAuctionsList.newBuilder()
                    .addAllAuctions(potentiallyInactiveAuctions)
                    .build();

            var response = bidServiceBlockingStub.checkAuctionsActivity(list);
            log.info("Received response from gRPC: {} auctions processed", response.getResultsCount());

            return response.getResultsMap();
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus(), e);
            GrpcExceptionHandler.handleException(e);
            return null;
        }
    }
}
