package van.karm.auction.infrastructure.enricher;

import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface AuctionFieldEnricher {
    void enrich(Map<String, Object> fieldsMap, Boolean isPrivate, Set<String> requestedFields, UUID auctionId);
    void enrich(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields);
}
