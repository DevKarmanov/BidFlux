package van.karm.auction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.AuctionInfo;
import van.karm.auction.dto.response.CreatedAuction;
import van.karm.auction.service.AuctionService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuctionControllerImpl implements AuctionController{
    private final AuctionService auctionService;

    @Override
    public CreatedAuction createAuction(CreateAuction auctionInfo) {
        return auctionService.createAuction(auctionInfo);
    }

    @Override
    public AuctionInfo auctionInfo(UUID id, String password) {
        return auctionService.getAuctionInfo(id, password);
    }
}
