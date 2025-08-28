package van.karm.bid.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AddBid(
        @NotNull(message = "auctionId is required")
        UUID auctionId,

        @NotNull(message = "Bid amount is required")
        BigDecimal amount
) {}

