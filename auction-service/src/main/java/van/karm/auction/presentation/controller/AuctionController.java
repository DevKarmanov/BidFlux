package van.karm.auction.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/auction")
@Validated
public interface AuctionController {
    
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

    //todo добавить администраторам изменять пароль (не забыть добавить опцию которая спрашивает должны ли все ввести пароль заново и если да, то обнулить список доверенных людей)
}
