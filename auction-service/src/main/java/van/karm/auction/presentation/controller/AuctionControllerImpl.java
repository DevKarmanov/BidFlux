package van.karm.auction.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.domain.service.AuctionService;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuctionControllerImpl implements AuctionController{
    private final AuctionService auctionService;

    @Override
    public ResponseEntity<CreatedAuction> createAuction(Jwt jwt, CreateAuction auctionInfo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(auctionService.createAuction(jwt, auctionInfo));
    }

    @Override
    public ResponseEntity<DynamicResponse> auctionInfo(Jwt jwt, UUID id, String password, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(auctionService.getAuctionInfo(jwt,id, password, fields));
    }

}
