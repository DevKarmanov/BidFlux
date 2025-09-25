package van.karm.auction.infrastructure.enricher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.infrastructure.grpc.client.UserClient;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Component
public class AuctionFieldEnricherImpl implements AuctionFieldEnricher {
    private final AuctionRepo auctionRepo;
    private final UserClient userClient;

    @Override
    public void enrich(Map<String, Object> fieldsMap, Boolean isPrivate, Set<String> requestedFields, UUID auctionId) {
        if (requestedFields != null && !requestedFields.isEmpty()) {
            boolean needAllowedUsersCount = requestedFields.contains("allowedUsersCount");
            boolean needOwnerName = requestedFields.contains("ownerName");

            if (Boolean.TRUE.equals(isPrivate) && needAllowedUsersCount) {
                fieldsMap.put("allowedUsersCount", auctionRepo.countAllowedUsersById(auctionId));
            }
            if (needOwnerName) {
                var ownerId = (UUID) fieldsMap.get("ownerId");
                String ownerName = userClient.getUsername(ownerId);
                if (ownerName != null) {
                    fieldsMap.put("ownerName", ownerName);
                }else throw new NullPointerException("For unknown reasons, username has become null");
            }
        }
    }

    @Override
    public void enrich(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        if (requestedFields == null || requestedFields.isEmpty()) {
            return;
        }

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            List<Future<?>> futures = new ArrayList<>();

            for (Map<String, Object> map : fieldsMap.getContent()) {
                futures.add(executor.submit(() -> {
                    Boolean isPrivate = (Boolean) map.get("isPrivate");
                    UUID auctionId = (UUID) map.get("id");
                    enrich(map, isPrivate, requestedFields, auctionId);
                    return null;
                }));
            }

            executor.shutdown();
            checkFuturesForErrors(futures);
        }
    }

    private void checkFuturesForErrors(List<Future<?>> futures){
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted while enriching auction data", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Error while enriching auction data", e.getCause());
            }
        }
    }

}
