package van.karm.bid.infrastructure.enricher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import van.karm.bid.application.enricher.FieldEnricher;
import van.karm.bid.application.grpc.UserGrpcClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Component
public class BidFieldEnricherImpl implements FieldEnricher {
    private final UserGrpcClient userGrpcClient;

    @Override
    public void enrich(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        if (requestedFields == null || requestedFields.isEmpty() || !requestedFields.contains("bidOwnerName")) {
            return;
        }

        try(ExecutorService executor = Executors.newFixedThreadPool(10)) {
            List<Callable<Void>> tasks = fieldsMap.getContent().stream()
                    .map(map -> (Callable<Void>) () -> {
                        String bidOwnerId = map.get("userId").toString();
                        map.put("bidOwnerName", userGrpcClient.getUsername(bidOwnerId));
                        return null;
                    })
                    .toList();

            checkFuturesForErrors(executor.invokeAll(tasks));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while enriching bidOwnerName", e);
        }
    }

    private void checkFuturesForErrors(List<Future<Void>> futures) {
        for (Future<Void> f : futures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Error fetching bidOwnerName via gRPC", e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting for task", e);
            }
        }
    }

}
