package van.karm.shared.infrastructure.query.runner;

import jakarta.persistence.Tuple;

import java.util.Map;

public interface QueryRunner {
    Tuple run(String jpql, Map<String, Object> params);
}
