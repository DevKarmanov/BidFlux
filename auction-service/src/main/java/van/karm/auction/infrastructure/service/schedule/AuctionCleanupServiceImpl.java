package van.karm.auction.infrastructure.service.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.application.service.schedule.AuctionCleanupService;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.infrastructure.processor.BatchProcessor;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuctionCleanupServiceImpl implements AuctionCleanupService {
    private final AuctionRepo auctionRepo;
    private final TransactionTemplate transactionTemplate;
    private final TaskScheduler taskScheduler;
    private final BatchProcessor batchProcessor;

    @Value("${settings.db.batch-size}")
    private int BATCH_SIZE;

    @Value("${settings.scheduler.cleaner.execute}")
    private boolean cleanAuctions;

    @Value("${settings.scheduler.cleaner.repeat-time-seconds}")
    private int cleanAuctionsRepeatTime;

    @PostConstruct
    public void scheduleTask() {
        if (cleanAuctions) {
            taskScheduler.scheduleAtFixedRate(this::runCleanup, Duration.ofSeconds(cleanAuctionsRepeatTime));
        }
    }

    @Override
    public void runCleanup() {
        transactionTemplate.executeWithoutResult(status -> {
            batchProcessor.process(auctionRepo.streamInactiveIds(),BATCH_SIZE,auctionRepo::deleteByIds);
            batchProcessor.process(auctionRepo.streamActiveWithDeletedUser(),BATCH_SIZE,auctionRepo::deleteByIds);
        });
    }
}
