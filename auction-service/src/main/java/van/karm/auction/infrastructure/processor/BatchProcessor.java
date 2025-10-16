package van.karm.auction.infrastructure.processor;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface BatchProcessor {
    <T> void process(Stream<T> stream, int batchSize, Consumer<List<T>> action);
}
