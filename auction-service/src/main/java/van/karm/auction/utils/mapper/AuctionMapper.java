package van.karm.auction.utils.mapper;

import van.karm.auction.dto.response.AuctionInfo;
import van.karm.auction.model.Auction;

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
