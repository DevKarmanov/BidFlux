package van.karm.bid.service.handler.auction;

import van.karm.bid.enums.Auction.AuctionStatus;
import van.karm.bid.exception.AuctionFinishedException;
import van.karm.bid.exception.AuctionInactiveException;

public class AuctionStatusHandler {

    public static void handle(final AuctionStatus status) {
        switch (status) {
            case INACTIVE -> throw new AuctionInactiveException("This auction no longer accepts bids and is closed");
            case FINISHED -> throw new AuctionFinishedException("This auction has ended and bids are no longer accepted");
        }
    }
}
