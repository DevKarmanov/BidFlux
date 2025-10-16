package van.karm.bid.application.validator;

import van.karm.auction.ValidateBidResponse;
import van.karm.bid.presentation.dto.request.AddBid;

public interface AuctionValidator {
    ValidateBidResponse isValid(String userId, AddBid bid);
}
