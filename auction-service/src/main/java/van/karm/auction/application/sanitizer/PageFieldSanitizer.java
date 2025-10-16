package van.karm.auction.application.sanitizer;

import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Set;

public interface PageFieldSanitizer {
    void sanitize(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields);
}
