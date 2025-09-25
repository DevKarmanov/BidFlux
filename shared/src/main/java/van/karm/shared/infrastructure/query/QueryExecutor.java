package van.karm.shared.infrastructure.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    <T> Page<Map<String, Object>> selectQueryByFieldPaged(Class<T> entityClass,
                                                          Map<String, Object> filters,
                                                          Set<String> requestedFields,
                                                          AllowedFieldsProvider allowedFieldsProvider,
                                                          FieldRule fieldRule,
                                                          Pageable pageable);
}
