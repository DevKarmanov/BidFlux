package van.karm.auction.infrastructure.enricher.auction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import van.karm.auction.application.enricher.FieldEnricher;
import van.karm.auction.application.enricher.PageFieldEnricher;

import java.util.Map;
import java.util.Set;

@Component
@Qualifier("auction-page-field-enricher")
public class AuctionPageFieldEnricherImpl implements PageFieldEnricher {
    private final FieldEnricher fieldEnricher;

    public AuctionPageFieldEnricherImpl(@Qualifier("auction-field-enricher") FieldEnricher fieldEnricher) {
        this.fieldEnricher = fieldEnricher;
    }

    @Override
    public void enrich(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        for (Map<String, Object> field : fieldsMap.getContent()) {
            fieldEnricher.enrich(field, requestedFields);
        }
    }
}
