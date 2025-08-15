package van.karm.auction.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.AuctionInfo;
import van.karm.auction.dto.response.CreatedAuction;

import java.util.UUID;

@RequestMapping("/auction")
public interface AuctionController {

    @PostMapping
    CreatedAuction createAuction(@Valid @RequestBody CreateAuction auctionInfo);

    @PostMapping("/{id}")
    AuctionInfo auctionInfo(@PathVariable UUID id, @RequestParam(required = false) String password);
}
