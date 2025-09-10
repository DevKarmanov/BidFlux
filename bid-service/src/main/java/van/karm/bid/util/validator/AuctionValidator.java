package van.karm.bid.util.validator;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import van.karm.auction.AuctionServiceGrpc;
import van.karm.auction.ValidateBidRequest;
import van.karm.bid.dto.request.AddBid;
import van.karm.bid.service.converter.MoneyConverter;
import van.karm.bid.service.handler.grpc.GrpcExceptionHandler;


@Component
public class AuctionValidator {

    @GrpcClient("auction-service")
    private AuctionServiceGrpc.AuctionServiceBlockingStub auctionStub;

    public boolean isValid(String userId, AddBid bid) {
        var request = ValidateBidRequest.newBuilder()
                .setUserId(userId)
                .setAuctionId(bid.auctionId().toString())
                .setAmount(MoneyConverter.toMoney(bid.amount()))
                .build();

        try {
            var response = auctionStub.validateBid(request);
            return response.getValid();
        } catch (StatusRuntimeException e) {
            GrpcExceptionHandler.handleException(e);
            return false;
        }
    }
}
