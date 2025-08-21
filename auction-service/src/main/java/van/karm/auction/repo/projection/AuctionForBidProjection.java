package van.karm.auction.repo.projection;

import van.karm.auction.model.AuctionStatus;
import van.karm.auction.model.CurrencyType;

import java.math.BigDecimal;
import java.util.UUID;

public interface AuctionForBidProjection {
    UUID getId();
    BigDecimal getStartPrice();
    BigDecimal getBidIncrement();
    Boolean getIsPrivate();
    AuctionStatus getStatus();
    CurrencyType getCurrency();
    BigDecimal getLastBid();
}
