package van.karm.shared.infrastructure.query;

import jakarta.persistence.Tuple;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.builder.QueryBuilder;
import van.karm.shared.infrastructure.query.runner.QueryRunner;
import van.karm.shared.infrastructure.query.selector.FieldSelector;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class QueryExecutorImpl implements QueryExecutor {
    private final FieldSelector fieldSelector;
    private final QueryBuilder queryBuilder;
    private final QueryRunner queryRunner;

    public QueryExecutorImpl(FieldSelector fieldSelector, QueryBuilder queryBuilder, QueryRunner queryRunner) {
        this.fieldSelector = fieldSelector;
        this.queryBuilder = queryBuilder;
        this.queryRunner = queryRunner;
    }

    @Override
    public <T> Map<String, Object> selectQueryByField(
            Class<T> entityClass,
            String fieldName,
            Object fieldValue,
            Set<String> requestedFields,
            AllowedFieldsProvider allowedFieldsProvider,
            FieldRule fieldRule) {

        Set<String> allowedFields = allowedFieldsProvider.getAllowedFields();
        Set<String> fieldsToSelect = fieldSelector.selectFields(requestedFields, allowedFields, fieldRule);

        if (fieldsToSelect.isEmpty()) return new LinkedHashMap<>();

        String jpql = queryBuilder.buildSelectQuery(entityClass, fieldName, fieldValue, fieldsToSelect);
        Tuple tuple = queryRunner.run(jpql, fieldValue);

        return fieldsToSelect.stream()
                .collect(LinkedHashMap::new, (map, f) -> map.put(f, tuple.get(f)), Map::putAll);
    }
}
