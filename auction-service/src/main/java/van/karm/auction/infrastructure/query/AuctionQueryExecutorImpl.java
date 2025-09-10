package van.karm.auction.infrastructure.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionQueryExecutorImpl implements QueryExecutor {
    private static final Logger log = LoggerFactory.getLogger(AuctionQueryExecutorImpl.class);
    private final EntityManager em;

    private static final Set<String> ALLOWED_AUCTION_FIELDS = Set.of(
            "id", "title", "description", "startPrice", "bidIncrement",
            "reservePrice", "isPrivate", "status", "startDate", "endDate",
            "currency"
    );

    @Override
    public Map<String, Object> selectQueryById(UUID entityId, Set<String> requestedFields) {
        log.info("executeSelectQueryById called with entityId={} and requestedFields={}", entityId, requestedFields);

        Set<String> fieldsToSelect = (requestedFields==null || requestedFields.isEmpty())
                ? new LinkedHashSet<>(ALLOWED_AUCTION_FIELDS)
                : requestedFields.stream()
                .filter(ALLOWED_AUCTION_FIELDS::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        fieldsToSelect.add("isPrivate");
        fieldsToSelect.add("accessCodeHash");

        if (requestedFields!=null && !requestedFields.isEmpty() && requestedFields.contains("ownerName")){
            fieldsToSelect.add("ownerId");
        }

        log.debug("Fields to select: {}", fieldsToSelect);

        String selectClause = fieldsToSelect.stream()
                .map(f -> "a." + f + " AS " + f)
                .collect(Collectors.joining(", "));

        String jpql = "SELECT " + selectClause + " FROM Auction a WHERE a.id = :id";
        log.debug("Generated JPQL: {}", jpql);

        Tuple tuple = em.createQuery(jpql, Tuple.class)
                .setParameter("id", entityId)
                .getResultList()
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Auction with id " + entityId + " not found"));

        Map<String, Object> resultMap = fieldsToSelect.stream()
                .collect(LinkedHashMap::new, (map, f) -> map.put(f, tuple.get(f)), Map::putAll);

        log.debug("Result map: {}", resultMap);

        return resultMap;
    }

}

