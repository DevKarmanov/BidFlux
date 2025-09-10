package van.karm.auction.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import van.karm.auction.domain.model.CurrencyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@With
@Schema(description = "DTO для создания нового аукциона")
public class CreateAuction {

        @NotBlank(message = "Auction title is required and cannot be blank")
        @Size(min = 3, max = 100, message = "Auction title must be between 3 and 100 characters")
        private String title;

        @NotBlank(message = "Auction description is required")
        @Size(min = 10, message = "Auction description must be at least 10 characters")
        private String description;

        @NotNull(message = "Starting price is required")
        @DecimalMin(value = "0.00", message = "Starting price must be ≥ 0.00")
        private BigDecimal startPrice;

        @NotNull(message = "Bid increment is required")
        @DecimalMin(value = "0.1", message = "Bid increment must be ≥ 0.1")
        private BigDecimal bidIncrement;

        @NotNull(message = "Reserve price is required")
        @DecimalMin(value = "0.1", message = "Reserve price must be ≥ 0.1")
        private BigDecimal reservePrice;

        @NotNull(message = "Private auction flag is required")
        private Boolean isPrivate;

        @NotNull(message = "Auction start date is required")
        @FutureOrPresent(message = "Auction start date must be in the present or future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime startDate;

        @NotNull(message = "Auction end date is required")
        @Future(message = "Auction end date must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endDate;

        @NotNull(message = "Currency is required")
        private CurrencyType currency;
}

