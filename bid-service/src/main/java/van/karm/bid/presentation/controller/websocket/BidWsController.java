package van.karm.bid.presentation.controller.websocket;

import java.security.Principal;

public interface BidWsController {
    void getLastBidByAuctionId(String auctionId, Principal principal);
}
