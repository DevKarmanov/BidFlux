package van.karm.bid.service;

import org.springframework.security.oauth2.jwt.Jwt;
import van.karm.bid.dto.request.AddBid;

public interface BidService {
    void addBid(Jwt jwt, AddBid bid);
}
