package van.karm.shared.infrastructure.query.builder;

import java.util.Map;
import java.util.Set;

public interface QueryBuilder {
    String buildSelectQuery(Class<?> entityClass, Map<String, Object> filters, Set<String> fieldsToSelect);
    String buildCountQuery(Class<?> entityClass, Map<String, Object> filters);
}
