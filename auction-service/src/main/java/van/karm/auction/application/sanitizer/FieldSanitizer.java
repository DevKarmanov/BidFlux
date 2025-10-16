package van.karm.auction.application.sanitizer;

import java.util.Map;
import java.util.Set;

public interface FieldSanitizer {
    void sanitize(Map<String, Object> fieldsMap, Set<String> requestedFields);
}
