package van.karm.shared.infrastructure.query.runner.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class JpaPagedQueryRunner implements PagedQueryRunner {

    private final EntityManager entityManager;

    public JpaPagedQueryRunner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Tuple> runPaged(String jpql, Map<String, Object> params, int offset, int limit) {
        TypedQuery<Tuple> query = entityManager.createQuery(jpql, Tuple.class)
                .setFirstResult(offset)
                .setMaxResults(limit);

        if (params != null) {
            params.forEach(query::setParameter);
        }

        List<Tuple> results = query.getResultList();
        return results != null ? results : Collections.emptyList();
    }


    @Override
    public long count(String countJpql, Map<String, Object> params) {
        TypedQuery<Long> query = entityManager.createQuery(countJpql, Long.class);
        params.forEach(query::setParameter);
        return query.getSingleResult();
    }
}
