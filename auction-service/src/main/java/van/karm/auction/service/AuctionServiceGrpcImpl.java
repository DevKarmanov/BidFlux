package van.karm.auction.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import van.karm.auction.AuctionServiceGrpc;
import van.karm.auction.GetAuctionRequest;
import van.karm.auction.GetAuctionResponse;
import van.karm.auction.repo.AuctionRepo;
import van.karm.auction.service.converter.MoneyConverter;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class AuctionServiceGrpcImpl extends AuctionServiceGrpc.AuctionServiceImplBase {
    private final AuctionRepo auctionRepo;

    @Override
    public void getAuctionInfo(GetAuctionRequest request, StreamObserver<GetAuctionResponse> responseObserver) {
        var auctionOpt = auctionRepo.findAuctionWithLastBid(UUID.fromString(request.getAuctionId()));
        if (auctionOpt.isEmpty()) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Auction with id " + request.getAuctionId() + " not found")
                            .asRuntimeException()
            );
            return;
        }

        var auction = auctionOpt.get();

        GetAuctionResponse response = GetAuctionResponse.newBuilder()
                .setId(auction.getId().toString())
                .setStartPrice(MoneyConverter.toMoney(auction.getStartPrice()))
                .setBidIncrement(MoneyConverter.toMoney(auction.getBidIncrement()))
                .setLastBidAmount(MoneyConverter.toMoney(auction.getLastBid()))
                .setIsPrivate(auction.getIsPrivate())
                .setStatus(auction.getStatus().name())
                .setCurrency(auction.getCurrency().name())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
