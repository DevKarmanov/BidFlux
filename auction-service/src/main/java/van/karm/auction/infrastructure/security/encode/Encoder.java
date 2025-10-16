package van.karm.auction.infrastructure.security.encode;

import van.karm.auction.domain.dto.AccessAndHashCodes;
import van.karm.auction.domain.dto.AccessCodeHash;

public interface Encoder {
    AccessAndHashCodes encode(boolean auctionIsPrivate);
    AccessCodeHash encode(String password);
}
