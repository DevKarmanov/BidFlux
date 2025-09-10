package van.karm.auction.infrastructure.sanitizer;

import java.util.Map;
import java.util.Set;

public interface AuctionFieldSanitizer {
    void sanitize(Map<String, Object> fieldsMap, Set<String> requestedFields);
}
