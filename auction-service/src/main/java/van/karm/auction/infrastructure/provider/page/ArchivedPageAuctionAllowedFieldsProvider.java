package van.karm.auction.infrastructure.provider.page;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.shared.application.provider.AllowedFieldsProvider;

import java.util.Set;

@Qualifier("archive-page-allowed-fields")
@Component
public class ArchivedPageAuctionAllowedFieldsProvider implements AllowedFieldsProvider {
    private static final Set<String> ALLOWED_AUCTION_FIELDS = Set.of(
            "id", "title",
            "reservePrice", "isPrivate", "status",
            "currency","ownerId","winnerId","startPrice"
    );

    @Override
    public Set<String> getAllowedFields() {
        return ALLOWED_AUCTION_FIELDS;
    }
}
