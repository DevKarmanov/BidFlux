package van.karm.auction.infrastructure.security.encode;

import van.karm.auction.domain.dto.AccessAndHashCodes;

public interface Encoder {
    AccessAndHashCodes encode(boolean auctionIsPrivate);
}
