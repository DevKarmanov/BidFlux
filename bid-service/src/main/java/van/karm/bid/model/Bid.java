package van.karm.bid.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Check(constraints = "amount >= auction.start_price AND amount % auction.bid_increment = 0")
@Table(
        indexes = {
                @Index(name = "idx_bid_auction_id",columnList = "auction_id"),
                @Index(name = "idx_bid_user_id",columnList = "user_id"),
                @Index(name = "idx_bid_auction_amount",columnList = "auction_id, amount")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId; //todo добавить FK в liquibase к таблице User

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "auction_id", nullable = false)
    private UUID auctionId;

}
