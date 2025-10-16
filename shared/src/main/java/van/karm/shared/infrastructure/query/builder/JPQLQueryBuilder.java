package van.karm.shared.infrastructure.query.builder;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JPQLQueryBuilder implements QueryBuilder {
    @Override
    public String buildSelectQuery(Class<?> entityClass, Map<String, Object> filters, Set<String> fieldsToSelect,LogicalOperator operator) {
        String alias = "e";

        String selectClause = fieldsToSelect.stream()
                .map(f -> alias + "." + f + " AS " + f)
                .collect(Collectors.joining(", "));

        String whereClause = filters.keySet().stream()
                .map(o -> alias + "." + o + " = :" + o)
                .collect(Collectors.joining(
                        operator == LogicalOperator.NONE ? "" : " " + operator.name() + " "
                ));

        return "SELECT " + selectClause +
                " FROM " + entityClass.getSimpleName() + " " + alias +
                (whereClause.isEmpty() ? "" : " WHERE " + whereClause);
    }

    public String buildCountQuery(Class<?> entityClass, Map<String, Object> filters) {
        String alias = "e";

        String whereClause = filters.keySet().stream()
                .map(f -> alias + "." + f + " = :" + f)
                .collect(Collectors.joining(" AND "));

        return "SELECT COUNT(" + alias + ") FROM " + entityClass.getSimpleName() + " " + alias +
                (whereClause.isEmpty() ? "" : " WHERE " + whereClause);
    }
}
