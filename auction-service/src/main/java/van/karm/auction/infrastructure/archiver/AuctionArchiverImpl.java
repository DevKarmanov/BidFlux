package van.karm.auction.infrastructure.archiver;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.application.archiver.AuctionArchiver;
import van.karm.auction.domain.repo.AuctionRepo;

@RequiredArgsConstructor
@Component
public class AuctionArchiverImpl implements AuctionArchiver {
    private final static Logger log = LoggerFactory.getLogger(AuctionArchiverImpl.class);

    private final AuctionRepo auctionRepo;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void archive() {
        transactionTemplate.executeWithoutResult(status -> {
            int archivedAuctionsCount = auctionRepo.moveFinishedAuctionsToArchive().size();
            log.info("Archived {} auctions", archivedAuctionsCount);
        });
    }
}
