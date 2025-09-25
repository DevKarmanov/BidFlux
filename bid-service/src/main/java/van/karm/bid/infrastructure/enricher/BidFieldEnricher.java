package van.karm.bid.infrastructure.enricher;

import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Set;

public interface BidFieldEnricher {
    void enrich(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields);
}
