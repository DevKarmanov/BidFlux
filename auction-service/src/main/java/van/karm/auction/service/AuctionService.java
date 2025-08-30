package van.karm.auction.service;

import org.springframework.security.oauth2.jwt.Jwt;
import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.AuctionInfo;
import van.karm.auction.dto.response.CreatedAuction;

import java.util.UUID;

public interface AuctionService {
    CreatedAuction createAuction(CreateAuction auctionInfo);
    AuctionInfo getAuctionInfo(Jwt jwt, UUID id, String password);
}
