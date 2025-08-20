package van.karm.bid.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AddBid(
        @NotNull(message = "auctionId is required")
        UUID auctionId,

        @NotNull(message = "Bid amount is required")
        @DecimalMin(value = "0.1", message = "The bid must be positive")
        BigDecimal amount
) {}

