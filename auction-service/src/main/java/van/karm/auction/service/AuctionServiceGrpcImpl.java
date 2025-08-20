package van.karm.auction.service;

import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import van.karm.auction.AuctionServiceGrpc;
import van.karm.auction.GetAuctionRequest;
import van.karm.auction.GetAuctionResponse;
import van.karm.auction.repo.AuctionRepo;

import java.time.ZoneOffset;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class AuctionServiceGrpcImpl extends AuctionServiceGrpc.AuctionServiceImplBase {
    private final AuctionRepo auctionRepo;

    @Override
    public void getAuctionInfo(GetAuctionRequest request, StreamObserver<GetAuctionResponse> responseObserver) {
        var auctionOpt = auctionRepo.findById(UUID.fromString(request.getAuctionId()));

        if (auctionOpt.isEmpty()) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Auction with id " + request.getAuctionId() + " not found")
                            .asRuntimeException()
            );
            return;
        }

        var auction = auctionOpt.get();

        Timestamp startTimestamp = Timestamp.newBuilder()
                .setSeconds(auction.getStartDate().toEpochSecond(ZoneOffset.UTC))
                .setNanos(auction.getStartDate().getNano())
                .build();

        Timestamp endTimestamp = Timestamp.newBuilder()
                .setSeconds(auction.getEndDate().toEpochSecond(ZoneOffset.UTC))
                .setNanos(auction.getEndDate().getNano())
                .build();

        GetAuctionResponse response = GetAuctionResponse.newBuilder()
                .setId(auction.getId().toString())
                .setStartPrice(auction.getStartPrice().doubleValue())
                .setBidIncrement(auction.getBidIncrement().doubleValue())
                .setReservePrice(auction.getReservePrice().doubleValue())
                .setIsPrivate(auction.isPrivate())
                .setStatus(auction.getStatus().name())
                .setStartDate(startTimestamp)
                .setEndDate(endTimestamp)
                .setCurrency(auction.getCurrency().name())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
