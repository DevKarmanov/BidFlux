package van.karm.auction.domain.service;

import org.springframework.security.oauth2.jwt.Jwt;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;

import java.util.Set;
import java.util.UUID;

public interface AuctionService {
    CreatedAuction createAuction(Jwt jwt, CreateAuction auctionInfo);
    DynamicResponse getAuctionInfo(Jwt jwt, UUID id, String password, Set<String> fields);
}
