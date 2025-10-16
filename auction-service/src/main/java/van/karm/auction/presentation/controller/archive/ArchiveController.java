package van.karm.auction.presentation.controller.archive;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/auction/archive")
@Validated
public interface ArchiveController {

    @GetMapping("/getAll")
    ResponseEntity<PagedResponse> getMyArchivedAuctions(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false,defaultValue = "5") int size,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @GetMapping("/{id}")
    ResponseEntity<DynamicResponse> getArchivedAuction(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable @NotNull(message = "Auction ID must not be null") UUID id,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll/{username}")
    ResponseEntity<PagedResponse> getUserArchivedAuctions(
            @PathVariable String username,
            @RequestParam(required = false,defaultValue = "5") int size,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/forced/{id}")
    ResponseEntity<Void> forcedDeleteArchivedAuction(
            @PathVariable @NotNull UUID id);

}
