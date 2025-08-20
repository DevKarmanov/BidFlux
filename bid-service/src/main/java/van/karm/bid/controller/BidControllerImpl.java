package van.karm.bid.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import van.karm.bid.dto.request.AddBid;
import van.karm.bid.service.BidService;

@RestController
@RequiredArgsConstructor
public class BidControllerImpl implements BidController {
    private final BidService bidService;

    @Override
    public ResponseEntity<Void> addBid(AddBid bid) {
        bidService.addBid(bid);
        return ResponseEntity
                .status(HttpStatus.CREATED).build();
    }
}
