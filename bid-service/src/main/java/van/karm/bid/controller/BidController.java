package van.karm.bid.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import van.karm.bid.dto.request.AddBid;

@RequestMapping("/bid")
@Validated
public interface BidController {

    @PostMapping
    ResponseEntity<Void> addBid(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid @NotNull(message = "Bid info must not be null") AddBid bid
    );
}

