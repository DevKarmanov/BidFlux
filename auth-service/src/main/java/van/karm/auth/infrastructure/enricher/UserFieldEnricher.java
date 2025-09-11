package van.karm.auth.infrastructure.enricher;

import java.util.Map;
import java.util.Set;

public interface UserFieldEnricher {
    void enrich(Map<String, Object> fieldsMap, Set<String> requestedFields, String username);
}
