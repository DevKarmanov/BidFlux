package van.karm.auction.infrastructure.service.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import van.karm.auction.application.archiver.AuctionArchiver;
import van.karm.auction.application.service.schedule.AuctionArchiverService;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AuctionArchiverServiceImpl implements AuctionArchiverService {
    private final static Logger log = LoggerFactory.getLogger(AuctionArchiverServiceImpl.class);

    private final AuctionArchiver auctionArchiver;
    private final TaskScheduler taskScheduler;

    @Value("${settings.scheduler.archiver.execute}")
    private boolean archiveAuctions;

    @Value("${settings.scheduler.archiver.repeat-time-days}")
    private int archiveAuctionsRepeatTime;

    @PostConstruct
    public void init() {
        if (archiveAuctions){
            taskScheduler.scheduleAtFixedRate(this::runArchiver, Duration.ofDays(archiveAuctionsRepeatTime));
        }
    }

    @Override
    public void runArchiver() {
        log.info("Archiver started");
        auctionArchiver.archive();
    }
}
