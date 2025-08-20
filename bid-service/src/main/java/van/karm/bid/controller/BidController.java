package van.karm.bid.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import van.karm.bid.dto.request.AddBid;

@RequestMapping("/bid")
public interface BidController {

    @PostMapping
    ResponseEntity<Void> addBid(@Valid @RequestBody AddBid bid);
}
