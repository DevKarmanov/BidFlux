package van.karm.auction.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import van.karm.auction.AuctionServiceGrpc;
import van.karm.auction.ValidateBidRequest;
import van.karm.auction.ValidateBidResponse;
import van.karm.auction.repo.AuctionRepo;
import van.karm.auction.service.converter.MoneyConverter;

import java.math.BigDecimal;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class AuctionServiceGrpcImpl extends AuctionServiceGrpc.AuctionServiceImplBase {
    private final AuctionRepo auctionRepo;

    @Override
    public void validateBid(ValidateBidRequest request, StreamObserver<ValidateBidResponse> responseObserver) {
        UUID auctionId = UUID.fromString(request.getAuctionId());

        if (!auctionRepo.existsById(auctionId)) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Auction with id " + request.getAuctionId() + " not found")
                            .asRuntimeException()
            );
            return;
        }

        UUID userId = UUID.fromString(request.getUserId());
        BigDecimal amount = MoneyConverter.fromMoney(request.getAmount());

        var exceptionMessage = auctionRepo.validateBid(auctionId, userId, amount);


        ValidateBidResponse response = ValidateBidResponse.newBuilder()
                .setValid(exceptionMessage == null)
                .setErrorMessage(exceptionMessage == null ? "" : exceptionMessage)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
