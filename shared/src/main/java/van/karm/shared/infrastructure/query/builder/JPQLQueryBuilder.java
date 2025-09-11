package van.karm.shared.infrastructure.query.builder;

import java.util.Set;
import java.util.stream.Collectors;

public class JPQLQueryBuilder implements QueryBuilder {
    @Override
    public String buildSelectQuery(Class<?> entityClass, String fieldName, Object value, Set<String> fieldsToSelect) {
        String alias = "e";
        String selectClause = fieldsToSelect.stream()
                .map(f -> alias + "." + f + " AS " + f)
                .collect(Collectors.joining(", "));
        return "SELECT " + selectClause + " FROM " + entityClass.getSimpleName() + " " + alias +
                " WHERE " + alias + "." + fieldName + " = :value";
    }
}
