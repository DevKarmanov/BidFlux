package van.karm.bid.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import van.karm.bid.presentation.dto.request.AddBid;
import van.karm.bid.application.service.BidService;
import van.karm.bid.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BidControllerImpl implements BidController {
    private final BidService bidService;

    @Override
    public ResponseEntity<Void> addBid(Jwt jwt, AddBid bid) {
        bidService.addBid(jwt,bid);
        return ResponseEntity
                .status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<PagedResponse> allBidsByAuctionId(Jwt jwt, UUID auctionId, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bidService.getAllBidsByAuctionId(jwt,auctionId,size,page,fields));
    }
}
