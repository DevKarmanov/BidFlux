package van.karm.auction.presentation.controller.auction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.auction.presentation.dto.request.ChangePasswordRequest;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/auction")
@Validated
public interface AuctionController {

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteAuction(
            @PathVariable @NotNull UUID id,
            @AuthenticationPrincipal Jwt jwt);

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/forced/{id}")
    ResponseEntity<Void> forcedDeleteAuction(
            @PathVariable @NotNull UUID id);

    
    @PostMapping
    ResponseEntity<CreatedAuction> createAuction(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid @NotNull(message = "Auction info must not be null") CreateAuction auctionInfo
    );

    @GetMapping("/{id}")
    ResponseEntity<DynamicResponse> auctionInfo(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable @NotNull(message = "Auction ID must not be null") UUID id,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Set<String> fields
            );

    @GetMapping("/getAll")
    ResponseEntity<PagedResponse> allAuctions(
            @RequestParam(required = false,defaultValue = "5") int size,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @PostMapping("/password/change/{id}")
    ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request
    );

}
