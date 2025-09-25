package van.karm.bid.infrastructure.sanitizer;

import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Set;

public interface BidFieldSanitizer {
    void sanitize(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields);
}
