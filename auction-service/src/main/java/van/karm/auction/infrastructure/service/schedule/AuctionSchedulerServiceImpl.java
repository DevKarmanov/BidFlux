package van.karm.auction.infrastructure.service.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.application.service.schedule.AuctionSchedulerService;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.domain.repo.projection.AuctionAndOwnerIds;
import van.karm.auction.infrastructure.grpc.client.bid.BidClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionSchedulerServiceImpl implements AuctionSchedulerService {
    private final Logger log = LoggerFactory.getLogger(AuctionSchedulerServiceImpl.class);
    private final AuctionRepo auctionRepo;
    private final TransactionTemplate transactionTemplate;
    private final TaskScheduler taskScheduler;
    private final BidClient bidClient;

    @Value("${settings.scheduler.finish.execute}")
    private boolean finishAuctions;

    @Value("${settings.scheduler.finish.repeat-time-seconds}")
    private int finishRepeatTime;

    @Value("${settings.scheduler.deactivate.execute}")
    private boolean deactivateAuctions;

    @Value("${settings.scheduler.deactivate.repeat-time-seconds}")
    private int deactivateAuctionsRepeatTime;

    @PostConstruct
    public void scheduleTask() {
        if (finishAuctions) {
            taskScheduler.scheduleAtFixedRate(this::finishExpiredAuctions, Duration.ofSeconds(finishRepeatTime));
        }

        if (deactivateAuctions) {
            taskScheduler.scheduleAtFixedRate(this::markAsInactive, Duration.ofSeconds(deactivateAuctionsRepeatTime));
        }
    }

//todo делать аукцион INACTIVE если за определенное время (смотреть дату начала и дату конца аукциона) не было сделано НИ ОДНОЙ ставки (или все ставки принадлежат создателю аукциона)
    @Override
    public void finishExpiredAuctions() {
        Integer updated = transactionTemplate.execute(status ->
                auctionRepo.finishExpiredAuctions()
        );

        if (updated != null && updated > 0) {
            log.info("Auctions closed: {}", updated);
        }
    }

    @Override
    public void markAsInactive() {
        log.info("Checking auctions for inactivity");

        List<AuctionAndOwnerIds> auctions = auctionRepo.getAllFinishedAuctions();
        if (!auctions.isEmpty()) {
            Map<String, Boolean> response = bidClient.checkInactive(auctions);

            transactionTemplate.executeWithoutResult(status -> auctionRepo.markFinishedAsChecked());

            List<UUID> toDeactivate = response.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(entry -> UUID.fromString(entry.getKey()))
                    .toList();

            if (!toDeactivate.isEmpty()) {
                log.info("Deactivating {} auctions", toDeactivate.size());

                for (int i = 0; i < toDeactivate.size(); i += 1000) {
                    List<UUID> batch = toDeactivate.subList(i, Math.min(i + 1000, toDeactivate.size()));
                    transactionTemplate.executeWithoutResult(status -> auctionRepo.updateStatusToInactive(batch));
                }
            }
        }else {
            log.info("No auctions found");
        }
    }
}
//todo дописать в openapi и gateway ендпоинт о получении пользователей