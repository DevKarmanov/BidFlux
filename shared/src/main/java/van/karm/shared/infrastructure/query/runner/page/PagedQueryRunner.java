package van.karm.shared.infrastructure.query.runner.page;

import jakarta.persistence.Tuple;

import java.util.List;
import java.util.Map;

public interface PagedQueryRunner {
    List<Tuple> runPaged(String jpql, Map<String, Object> params, int offset, int limit);
    long count(String countJpql, Map<String, Object> params);
}