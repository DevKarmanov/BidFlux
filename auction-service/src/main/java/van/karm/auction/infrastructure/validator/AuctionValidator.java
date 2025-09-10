package van.karm.auction.infrastructure.validator;

import java.util.UUID;

public interface AuctionValidator {
    void validate(String password, Boolean isPrivate, UUID auctionId, UUID userId, String accessCodeHash);
}
