package van.karm.auction.infrastructure.service.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.application.service.schedule.AuctionSchedulerService;
import van.karm.auction.domain.repo.AuctionRepo;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuctionSchedulerServiceImpl implements AuctionSchedulerService {
    private final Logger log = LoggerFactory.getLogger(AuctionSchedulerServiceImpl.class);
    private final AuctionRepo auctionRepo;
    private final TransactionTemplate transactionTemplate;
    private final TaskScheduler taskScheduler;

    @PostConstruct
    public void scheduleTask() {
        taskScheduler.scheduleAtFixedRate(this::finishExpiredAuctions, Duration.ofSeconds(10));
    }

    @Override
    public void finishExpiredAuctions() {
        Integer updated = transactionTemplate.execute(status ->
                auctionRepo.finishExpiredAuctions()
        );

        if (updated != null && updated > 0) {
            log.info("Auctions closed: {}", updated);
        }
    }
}
