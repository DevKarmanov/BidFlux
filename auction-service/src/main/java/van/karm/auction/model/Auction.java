package van.karm.auction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Check(constraints = "start_date < end_date AND reserve_price >= start_price")
@Table(
        indexes = {
                @Index(name = "idx_status_end_date", columnList = "status, end_date"),
                @Index(name = "idx_start_price", columnList = "start_price"),
                @Index(name = "idx_start_date", columnList = "start_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 100)
    @Size(min = 3, max = 100)
    private String title;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    @Size(min = 10)
    private String description;

    @Column(name = "start_price", nullable = false, precision = 19, scale = 4)
    @DecimalMin("0.00")
    private BigDecimal startPrice;

    @Column(name = "bid_increment", nullable = false, precision = 19, scale = 4)
    @DecimalMin("0.1")
    private BigDecimal bidIncrement;

    @Column(name = "reserve_price", nullable = false, precision = 19, scale = 4)
    @DecimalMin("0.1")
    private BigDecimal reservePrice;

    @Builder.Default
    @Column(name = "is_private", columnDefinition = "boolean", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "access_code_hash")
    private String accessCodeHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuctionStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private CurrencyType currency;

    public Auction(String title, String description, BigDecimal startPrice, BigDecimal bidIncrement, BigDecimal reservePrice, boolean isPrivate, String accessCodeHash, AuctionStatus status, LocalDateTime startDate, LocalDateTime endDate, CurrencyType currency) {
        this.title = title;
        this.description = description;
        this.startPrice = startPrice;
        this.bidIncrement = bidIncrement;
        this.reservePrice = reservePrice;
        this.isPrivate = isPrivate;
        this.accessCodeHash = accessCodeHash;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currency = currency;
    }
}

