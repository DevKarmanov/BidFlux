package van.karm.bid.domain.factory;

import van.karm.bid.presentation.dto.request.AddBid;
import van.karm.bid.domain.model.Bid;

import java.time.LocalDateTime;
import java.util.UUID;

public class BidFactory {
    public static Bid create(String userId, AddBid bid) {
        return Bid.builder()
                .amount(bid.amount())
                .auctionId(bid.auctionId())
                .createdAt(LocalDateTime.now())
                .userId(UUID.fromString(userId))
                .build();
    }
}
