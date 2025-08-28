package van.karm.bid.service;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import van.karm.auction.AuctionServiceGrpc;
import van.karm.auction.GetAuctionRequest;
import van.karm.auction.GetAuctionResponse;
import van.karm.bid.dto.request.AddBid;
import van.karm.bid.enums.Auction.AuctionStatus;
import van.karm.bid.exception.AccessDeniedException;
import van.karm.bid.exception.BidAmountException;
import van.karm.bid.model.Bid;
import van.karm.bid.repo.BidRepo;
import van.karm.bid.service.converter.MoneyConverter;
import van.karm.bid.service.handler.auction.AuctionStatusHandler;
import van.karm.bid.service.handler.grpc.GrpcExceptionHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidRepo bidRepo;

    @GrpcClient("auction-service")
    private AuctionServiceGrpc.AuctionServiceBlockingStub auctionStub;

    @Transactional
    @Override
    public void addBid(Jwt jwt, AddBid bid) {
       String userId = jwt.getClaim("userId");

        GetAuctionRequest request = GetAuctionRequest.newBuilder()
                .setUserId(userId)
                .setAuctionId(bid.auctionId().toString())
                .build();

        try {
            GetAuctionResponse response = auctionStub.getAuctionInfo(request);

            var status = AuctionStatus.valueOf(response.getStatus());
            if (status == AuctionStatus.ACTIVE) {
               validateBidAmount(bid,response);
                if (!response.getAllowed()){
                    throw new AccessDeniedException("You have been denied the opportunity to place a bet");
                }

                Bid newBid = Bid.builder()
                        .amount(bid.amount())
                        .auctionId(bid.auctionId())
                        .createdAt(LocalDateTime.now())
                        .userId(UUID.fromString(userId))
                        .build();

                bidRepo.save(newBid);
            } else {
                AuctionStatusHandler.handle(status);
            }

        }catch (StatusRuntimeException e){
            GrpcExceptionHandler.handleException(e);
        }
    }

    private void validateBidAmount(AddBid bid, GetAuctionResponse response) {
        BigDecimal bidIncrement = MoneyConverter.fromMoney(response.getBidIncrement());
        BigDecimal startPrice = MoneyConverter.fromMoney(response.getStartPrice());
        BigDecimal lastBid = MoneyConverter.fromMoney(response.getLastBidAmount());

        boolean currentBidBiggerThenBidIncrement = bid.amount().compareTo(bidIncrement)>=0;
        boolean currentBidBiggerThenPrevious = bid.amount().compareTo(lastBid)>0;
        boolean currentBidBiggerOrEqualsStartPrice = bid.amount().compareTo(startPrice)>=0;

        if (!currentBidBiggerThenBidIncrement){
            throw new BidAmountException("The minimum bid step is "+bidIncrement);
        }
        if (!currentBidBiggerThenPrevious){
            throw new BidAmountException("The bid must exceed the previous one");
        }
        if (!currentBidBiggerOrEqualsStartPrice){
            throw new BidAmountException("The bid must match or exceed the specified starting bid");
        }
    }
}
