package van.karm.bid.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.bid.presentation.dto.request.AddBid;
import van.karm.bid.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/bid")
@Validated
public interface BidController {

    @PostMapping
    ResponseEntity<Void> addBid(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid @NotNull(message = "Bid info must not be null") AddBid bid
    );

    @GetMapping("/getAll/{auctionId}")
    ResponseEntity<PagedResponse> allBidsByAuctionId(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID auctionId,
            @RequestParam(required = false,defaultValue = "5") int size,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields);
}

