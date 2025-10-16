package van.karm.bid.application.sanitizer;

import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Set;

public interface FieldSanitizer {
    void sanitize(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields);
}
