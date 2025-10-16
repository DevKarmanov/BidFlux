package van.karm.complaint.application.enricher;

import java.util.Map;
import java.util.Set;

public interface FieldEnricher {
    void enrich(Map<String, Object> fieldsMap, Set<String> requestedFields);
}
