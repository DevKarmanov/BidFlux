package van.karm.shared.infrastructure.query;

import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;
import van.karm.shared.infrastructure.query.builder.QueryBuilder;
import van.karm.shared.infrastructure.query.runner.QueryRunner;
import van.karm.shared.infrastructure.query.runner.page.PagedQueryRunner;
import van.karm.shared.infrastructure.query.selector.FieldSelector;

import java.util.*;


public class QueryExecutorImpl implements QueryExecutor {
    private final FieldSelector fieldSelector;
    private final QueryBuilder queryBuilder;
    private final QueryRunner queryRunner;
    private final PagedQueryRunner pagedQueryRunner;

    public QueryExecutorImpl(FieldSelector fieldSelector, QueryBuilder queryBuilder, QueryRunner queryRunner, PagedQueryRunner pagedQueryRunner) {
        this.fieldSelector = fieldSelector;
        this.queryBuilder = queryBuilder;
        this.queryRunner = queryRunner;
        this.pagedQueryRunner = pagedQueryRunner;
    }

    @Override
    public <T> Map<String, Object> selectQueryByField(
            Class<T> entityClass,
            Map<String, Object> filter,
            LogicalOperator logicalOperator,
            Set<String> requestedFields,
            AllowedFieldsProvider allowedFieldsProvider,
            FieldRule fieldRule) {

        Set<String> allowedFields = allowedFieldsProvider.getAllowedFields();
        Set<String> fieldsToSelect = fieldSelector.selectFields(requestedFields, allowedFields, fieldRule);

        if (fieldsToSelect.isEmpty()) return new LinkedHashMap<>();

        String jpql = queryBuilder.buildSelectQuery(entityClass, filter, fieldsToSelect, logicalOperator);
        Tuple tuple = queryRunner.run(jpql, filter);

        return fieldsToSelect.stream()
                .collect(LinkedHashMap::new, (map, f) -> map.put(f, tuple.get(f)), Map::putAll);
    }

    @Override
    public <T> Page<Map<String, Object>> selectQueryByFieldPaged(
            Class<T> entityClass,
            Map<String, Object> filters,
            LogicalOperator logicalOperator,
            Set<String> requestedFields,
            AllowedFieldsProvider allowedFieldsProvider,
            FieldRule fieldRule,
            Pageable pageable) {

        Set<String> allowedFields = allowedFieldsProvider.getAllowedFields();
        Set<String> fieldsToSelect = fieldSelector.selectFields(requestedFields, allowedFields, fieldRule);

        if (fieldsToSelect.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        String jpql = queryBuilder.buildSelectQuery(entityClass, filters, fieldsToSelect, logicalOperator);
        List<Tuple> tuples = pagedQueryRunner.runPaged(jpql, filters,
                (int) pageable.getOffset(), pageable.getPageSize());

        List<Map<String, Object>> content = tuples.stream()
                .map(tuple -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    for (String f : fieldsToSelect) {
                        map.put(f, tuple.get(f));
                    }
                    return map;
                })
                .toList();

        String countJpql = queryBuilder.buildCountQuery(entityClass, filters);
        long total = pagedQueryRunner.count(countJpql, filters);

        return new PageImpl<>(content, pageable, total);
    }
}
