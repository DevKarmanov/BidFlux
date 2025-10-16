package van.karm.auction.infrastructure.provider;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.shared.application.provider.AllowedFieldsProvider;

import java.util.Set;

@Qualifier("archive-allowed-fields")
@Component
public class ArchivedAuctionAllowedFieldsProvider implements AllowedFieldsProvider {
    private static final Set<String> ALLOWED_AUCTION_FIELDS = Set.of(
            "id", "title", "description", "startPrice", "bidIncrement",
            "reservePrice", "isPrivate", "status", "startDate", "endDate",
            "currency","ownerId","winnerId","finalAmount"
    );

    @Override
    public Set<String> getAllowedFields() {
        return ALLOWED_AUCTION_FIELDS;
    }
}
