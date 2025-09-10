package van.karm.auction.infrastructure.mapper;

import van.karm.auction.presentation.dto.response.AuctionInfo;
import van.karm.auction.domain.model.Auction;

public class AuctionMapper {
    public static AuctionInfo toAuctionInfo(Auction auction, boolean isPrivate) {
        return new AuctionInfo(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getStartPrice(),
                auction.getBidIncrement(),
                auction.getReservePrice(),
                isPrivate,
                auction.getStatus(),
                auction.getStartDate(),
                auction.getEndDate(),
                auction.getCurrency()
        );
    }
}
