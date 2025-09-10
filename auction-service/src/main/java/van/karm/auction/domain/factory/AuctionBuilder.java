package van.karm.auction.domain.factory;

import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.domain.model.Auction;
import van.karm.auction.domain.model.AuctionStatus;

import java.util.UUID;

public class AuctionBuilder {
    public static Auction buildAuction(UUID ownerId, CreateAuction auctionInfo, String accessCodeHash) {
        return new Auction(
                ownerId,
                auctionInfo.getTitle(),
                auctionInfo.getDescription(),
                auctionInfo.getStartPrice(),
                auctionInfo.getBidIncrement(),
                auctionInfo.getReservePrice(),
                auctionInfo.getIsPrivate(),
                accessCodeHash,
                AuctionStatus.ACTIVE,     //todo брать время окончания аукциона и запланировать автоматическую установку его статуса на FINISHED
                auctionInfo.getStartDate(),
                auctionInfo.getEndDate(),
                auctionInfo.getCurrency()
        );
    }
}
