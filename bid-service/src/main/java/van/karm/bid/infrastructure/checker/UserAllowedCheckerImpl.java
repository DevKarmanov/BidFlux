package van.karm.bid.infrastructure.checker;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import van.karm.auction.AuctionServiceGrpc;
import van.karm.auction.CheckAllowedRequest;
import van.karm.bid.application.checker.UserAllowedChecker;
import van.karm.bid.infrastructure.grpc.exception.handler.GrpcExceptionHandler;

import java.util.UUID;

@Component
public class UserAllowedCheckerImpl implements UserAllowedChecker {

    @GrpcClient("auction-service")
    private AuctionServiceGrpc.AuctionServiceBlockingStub auctionStub;

    @Override
    public boolean isUserAllowed(String userId, UUID auctionId) {
        var request = CheckAllowedRequest.newBuilder()
                .setUserId(userId)
                .setAuctionId(auctionId.toString())
                .build();

        try {
            var response = auctionStub.checkAllowed(request);
            return response.getAllowed();
        } catch (StatusRuntimeException e) {
            GrpcExceptionHandler.handleException(e);
            return false;
        }
    }
}
