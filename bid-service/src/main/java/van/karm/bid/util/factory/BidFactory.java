package van.karm.bid.util.factory;

import van.karm.bid.dto.request.AddBid;
import van.karm.bid.model.Bid;

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
