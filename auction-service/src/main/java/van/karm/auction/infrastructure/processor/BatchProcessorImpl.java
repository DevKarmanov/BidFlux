package van.karm.auction.infrastructure.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class BatchProcessorImpl implements BatchProcessor {
    private final static Logger log = LoggerFactory.getLogger(BatchProcessorImpl.class);

    @Override
    public <T> void process(Stream<T> stream, int batchSize, Consumer<List<T>> action) {
        try (stream) {
            List<T> batch = new ArrayList<>(batchSize);

            stream.forEach(item -> {
                batch.add(item);
                if (batch.size() >= batchSize) {
                    log.info("Processing batch of {} items", batch.size());
                    action.accept(batch);
                    batch.clear();
                }
            });

            if (!batch.isEmpty()) {
                log.info("Processing final batch of {} items", batch.size());
                action.accept(batch);
            }
        } catch (Exception e) {
            log.error("Error while processing stream", e);
            throw e;
        }
    }
}
