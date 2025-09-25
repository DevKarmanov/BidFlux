package van.karm.shared.infrastructure.query.runner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JpaQueryRunner implements QueryRunner{
    private final static Logger log = LoggerFactory.getLogger(JpaQueryRunner.class);
    private final EntityManager em;

    public JpaQueryRunner(EntityManager em) {
        this.em = em;
    }

    @Override
    public Tuple run(String jpql, Map<String, Object> params) {
        TypedQuery<Tuple> query = em.createQuery(jpql, Tuple.class);
        params.forEach(query::setParameter);  // Устанавливаем параметры по имени
        return query.getResultList().stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Entity not found for query: {}", jpql);
                    return new EntityNotFoundException("Entity not found");
                });
    }
}
