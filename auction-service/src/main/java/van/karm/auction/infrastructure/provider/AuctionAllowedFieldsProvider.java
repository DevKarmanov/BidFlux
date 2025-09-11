package van.karm.auction.infrastructure.provider;

import org.springframework.stereotype.Component;
import van.karm.shared.application.provider.AllowedFieldsProvider;

import java.util.Set;

@Component
public class AuctionAllowedFieldsProvider implements AllowedFieldsProvider {
    private static final Set<String> ALLOWED_AUCTION_FIELDS = Set.of(
            "id", "title", "description", "startPrice", "bidIncrement",
            "reservePrice", "isPrivate", "status", "startDate", "endDate",
            "currency"
    );

    @Override
    public Set<String> getAllowedFields() {
        return ALLOWED_AUCTION_FIELDS;
    }
}
