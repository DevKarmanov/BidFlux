package van.karm.shared.infrastructure.query.builder;

import java.util.Set;

public interface QueryBuilder {
    String buildSelectQuery(Class<?> entityClass, String fieldName, Object value, Set<String> fieldsToSelect);
}
