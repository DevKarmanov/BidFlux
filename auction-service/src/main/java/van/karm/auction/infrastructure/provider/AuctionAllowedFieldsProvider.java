package van.karm.auction.infrastructure.provider;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.shared.application.provider.AllowedFieldsProvider;

import java.util.Set;

@Qualifier("auction-allowed-fields")
@Component
public class AuctionAllowedFieldsProvider implements AllowedFieldsProvider {
    private static final Set<String> ALLOWED_AUCTION_FIELDS = Set.of(
            "id", "title", "description", "startPrice", "bidIncrement",
            "reservePrice", "isPrivate", "status", "startDate", "endDate",
            "currency","finalAmount"
    );

    @Override
    public Set<String> getAllowedFields() {
        return ALLOWED_AUCTION_FIELDS;
    }
}
