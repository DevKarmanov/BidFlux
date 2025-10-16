package van.karm.bid.infrastructure.validator;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import van.karm.auction.AuctionServiceGrpc;
import van.karm.auction.ValidateBidRequest;
import van.karm.auction.ValidateBidResponse;
import van.karm.bid.application.validator.AuctionValidator;
import van.karm.bid.presentation.dto.request.AddBid;
import van.karm.bid.infrastructure.converter.MoneyConverter;
import van.karm.bid.infrastructure.grpc.exception.handler.GrpcExceptionHandler;


@Component
public class AuctionValidatorImpl implements AuctionValidator {

    @GrpcClient("auction-service")
    private AuctionServiceGrpc.AuctionServiceBlockingStub auctionStub;

    @Override
    public ValidateBidResponse isValid(String userId, AddBid bid) {
        var request = ValidateBidRequest.newBuilder()
                .setUserId(userId)
                .setAuctionId(bid.auctionId().toString())
                .setAmount(MoneyConverter.toMoney(bid.amount()))
                .build();

        try {
            return auctionStub.validateBid(request);
        } catch (StatusRuntimeException e) {
            GrpcExceptionHandler.handleException(e);
            return null;
        }
    }
}
