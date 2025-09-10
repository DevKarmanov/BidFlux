package van.karm.auction.infrastructure.query;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface QueryExecutor {
    Map<String, Object> selectQueryById(UUID entityId, Set<String> requiredFields);
}
