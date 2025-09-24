package van.karm.bid.application.validator;

import van.karm.bid.presentation.dto.request.AddBid;

public interface AuctionValidator {
    boolean isValid(String userId, AddBid bid);
}
