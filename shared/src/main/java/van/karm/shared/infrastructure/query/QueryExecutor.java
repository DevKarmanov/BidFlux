package van.karm.shared.infrastructure.query;

import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;

import java.util.Map;
import java.util.Set;

public interface QueryExecutor {
    <T> Map<String, Object> selectQueryByField(Class<T> entityClass,
                                                String fieldName,
                                                Object fieldValue,
                                                Set<String> requestedFields,
                                                AllowedFieldsProvider allowedFieldsProvider,
                                                FieldRule fieldRule);
}
