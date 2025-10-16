package van.karm.complaint.application.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;

import java.util.Map;
import java.util.Set;

public interface EntityQueryService<T> {
    Map<String, Object> getSingle(Map<String, Object> filters, LogicalOperator logicalOperator, Set<String> fields);
    Page<Map<String, Object>> getPaged(Map<String, Object> filters, LogicalOperator op, Set<String> fields, Pageable pageable);
}