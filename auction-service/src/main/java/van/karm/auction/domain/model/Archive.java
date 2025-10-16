package van.karm.auction.domain.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "archived_auctions",
        indexes = {
                @Index(name = "idx_archived_status_end_date", columnList = "status, endDate"),
                @Index(name = "idx_archived_start_price", columnList = "startPrice")
        })
@AllArgsConstructor
@NoArgsConstructor
public class Archive{

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    @Size(min = 3, max = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    @Size(min = 10)
    private String description;

    @Column(nullable = false, precision = 19, scale = 4)
    @DecimalMin("0.0")
    private BigDecimal startPrice;

    @Column(nullable = false, precision = 19, scale = 4)
    @DecimalMin("0.1")
    private BigDecimal bidIncrement;

    @Column(nullable = false, precision = 19, scale = 4)
    @DecimalMin("0.1")
    private BigDecimal reservePrice;

    @Column(nullable = false)
    private boolean isPrivate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyType currency;

    @Column
    private UUID winnerId;

    @Column
    private BigDecimal finalAmount;
}
