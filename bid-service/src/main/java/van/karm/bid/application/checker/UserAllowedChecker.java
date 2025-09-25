package van.karm.bid.application.checker;

import java.util.UUID;

public interface UserAllowedChecker {
    boolean isUserAllowed(String userId, UUID auctionId);
}
