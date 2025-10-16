package van.karm.complaint.infrastructure.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import van.karm.complaint.application.service.query.EntityQueryService;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.QueryExecutor;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;

import java.util.Map;
import java.util.Set;


public class GenericEntityQueryService<T> implements EntityQueryService<T> {
    private final QueryExecutor executor;
    private final AllowedFieldsProvider fieldsProvider;
    private final AllowedFieldsProvider pageFieldsProvider;
    private final FieldRule rule;
    private final Class<T> entityClass;

    public GenericEntityQueryService(QueryExecutor executor,
                                     AllowedFieldsProvider fieldsProvider,
                                     AllowedFieldsProvider pageFieldsProvider,
                                     FieldRule rule,
                                     Class<T> entityClass) {
        this.executor = executor;
        this.fieldsProvider = fieldsProvider;
        this.pageFieldsProvider = pageFieldsProvider;
        this.rule = rule;
        this.entityClass = entityClass;
    }

    @Override
    public Map<String, Object> getSingle(Map<String, Object> filters, LogicalOperator logicalOperator, Set<String> fields) {
        return executor.selectQueryByField(
                entityClass,
                filters,
                logicalOperator,
                fields,
                fieldsProvider,
                rule
        );
    }

    @Override
    public Page<Map<String, Object>> getPaged(Map<String, Object> filters, LogicalOperator op, Set<String> fields, Pageable pageable) {
        return executor.selectQueryByFieldPaged(
                entityClass,
                filters,
                op,
                fields,
                pageFieldsProvider,
                rule,
                pageable
        );
    }
}
