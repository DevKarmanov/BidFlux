package van.karm.bid.service;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.AuctionServiceGrpc;
import van.karm.auction.ValidateBidRequest;
import van.karm.bid.dto.request.AddBid;
import van.karm.bid.exception.AccessDeniedException;
import van.karm.bid.model.Bid;
import van.karm.bid.repo.BidRepo;
import van.karm.bid.service.converter.MoneyConverter;
import van.karm.bid.service.handler.grpc.GrpcExceptionHandler;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidRepo bidRepo;
    private final TransactionTemplate transactionTemplate;

    @GrpcClient("auction-service")
    private AuctionServiceGrpc.AuctionServiceBlockingStub auctionStub;

    @Override
    public void addBid(Jwt jwt, AddBid bid) {
        String userId = jwt.getClaim("userId");

        var validateBidRequest = ValidateBidRequest.newBuilder()
                .setUserId(userId)
                .setAuctionId(bid.auctionId().toString())
                .setAmount(MoneyConverter.toMoney(bid.amount()))
                .build();

        try {
            var response = auctionStub.validateBid(validateBidRequest);

            if (!response.getValid()) {
                throw new AccessDeniedException(response.getErrorMessage());
            }

            LocalDateTime now = LocalDateTime.now();
            UUID userUuid = UUID.fromString(userId);

            Bid newBid = Bid.builder()
                    .amount(bid.amount())
                    .auctionId(bid.auctionId())
                    .createdAt(now)
                    .userId(userUuid)
                    .build();

            transactionTemplate.executeWithoutResult(statusTx -> bidRepo.save(newBid));

        } catch (StatusRuntimeException e) {
            GrpcExceptionHandler.handleException(e);
        }
    }

}
