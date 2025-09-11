package van.karm.shared.infrastructure.query.runner;

import jakarta.persistence.Tuple;

public interface QueryRunner {
    Tuple run(String jpql, Object value);
}
