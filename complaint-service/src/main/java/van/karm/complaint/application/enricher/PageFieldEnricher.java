package van.karm.complaint.application.enricher;

import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Set;

public interface PageFieldEnricher {
    void enrich(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields);
}
