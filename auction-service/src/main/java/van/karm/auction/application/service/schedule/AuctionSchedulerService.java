package van.karm.auction.application.service.schedule;

public interface AuctionSchedulerService {
    void finishExpiredAuctions();
    void markAsInactive();
}
