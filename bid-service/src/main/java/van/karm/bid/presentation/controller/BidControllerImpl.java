package van.karm.bid.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import van.karm.bid.presentation.dto.request.AddBid;
import van.karm.bid.application.service.BidService;

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
}
