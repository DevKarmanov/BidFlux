package van.karm.auction.service;

import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.AuctionInfo;
import van.karm.auction.dto.response.CreatedAuction;

import java.util.UUID;

public interface AuctionService {
    CreatedAuction createAuction(CreateAuction auctionInfo);
    AuctionInfo getAuctionInfo(UUID id, String password);
}
