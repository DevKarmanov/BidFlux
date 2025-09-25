package van.karm.auction.infrastructure.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import van.karm.auction.*;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.infrastructure.mapper.MoneyMapper;

import java.math.BigDecimal;
import java.util.Optional;
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
        BigDecimal amount = MoneyMapper.fromMoney(request.getAmount());

        var exceptionMessage = auctionRepo.validateBid(auctionId, userId, amount);


        ValidateBidResponse response = ValidateBidResponse.newBuilder()
                .setValid(exceptionMessage == null)
                .setErrorMessage(exceptionMessage == null ? "" : exceptionMessage)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkAllowed(CheckAllowedRequest request, StreamObserver<CheckAllowedResponse> responseObserver) {
        UUID auctionId = UUID.fromString(request.getAuctionId());
        UUID userId = UUID.fromString(request.getUserId());

        Optional<Boolean> allowedOpt = auctionRepo.findAllowed(auctionId, userId);

        if (allowedOpt.isEmpty()) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Auction not found: " + auctionId)
                            .asRuntimeException()
            );
            return;
        }

        CheckAllowedResponse response = CheckAllowedResponse.newBuilder()
                .setAllowed(allowedOpt.get())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
