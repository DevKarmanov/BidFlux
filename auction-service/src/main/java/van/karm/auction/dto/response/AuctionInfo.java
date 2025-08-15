package van.karm.auction.dto.response;

import van.karm.auction.model.AuctionStatus;
import van.karm.auction.model.CurrencyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionInfo(
        UUID id,
        String title,
        String description,
        BigDecimal startPrice,
        BigDecimal bidIncrement,
        BigDecimal reservePrice,
        boolean isPrivate,
        AuctionStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        CurrencyType currency
) {
}
