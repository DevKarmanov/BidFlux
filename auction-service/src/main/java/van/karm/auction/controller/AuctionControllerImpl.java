package van.karm.auction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<CreatedAuction> createAuction(CreateAuction auctionInfo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(auctionService.createAuction(auctionInfo));
    }

    @Override
    public ResponseEntity<AuctionInfo> auctionInfo(Jwt jwt, UUID id, String password) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(auctionService.getAuctionInfo(jwt,id, password));
    }

}
