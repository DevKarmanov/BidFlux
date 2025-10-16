package van.karm.auction.presentation.controller.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auction.application.service.AuctionService;
import van.karm.auction.presentation.dto.request.ChangePasswordRequest;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuctionControllerImpl implements AuctionController {
    private final AuctionService auctionService;

    @Override
    public ResponseEntity<Void> deleteAuction(UUID id, Jwt jwt) {
        auctionService.deleteAuction(id, jwt);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<Void> forcedDeleteAuction(UUID id) {
        auctionService.forcedDeleteAuction(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

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

    @Override
    public ResponseEntity<PagedResponse> allAuctions(int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(auctionService.getAllAuctions(fields,size,page));
    }

    @Override
    public ResponseEntity<Void> changePassword(Jwt jwt, UUID id, ChangePasswordRequest request) {
        auctionService.changePassword(jwt, id, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
