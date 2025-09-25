package van.karm.bid.application.service;

import org.springframework.security.oauth2.jwt.Jwt;
import van.karm.bid.presentation.dto.request.AddBid;
import van.karm.bid.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

public interface BidService {
    void addBid(Jwt jwt, AddBid bid);
    PagedResponse getAllBidsByAuctionId(Jwt jwt, UUID auctionId, int size, int page, Set<String> fields);
}
