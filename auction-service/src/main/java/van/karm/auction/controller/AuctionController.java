package van.karm.auction.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.AuctionInfo;
import van.karm.auction.dto.response.CreatedAuction;

import java.util.UUID;

@RequestMapping("/auction")
@Validated
public interface AuctionController {

    //todo сделать swagger документацию
    @PostMapping
    ResponseEntity<CreatedAuction> createAuction(
            @RequestBody @Valid @NotNull(message = "Auction info must not be null") CreateAuction auctionInfo
    );

    @PostMapping("/{id}")
    ResponseEntity<AuctionInfo> auctionInfo(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable @NotNull(message = "Auction ID must not be null") UUID id,
            @RequestParam(required = false) String password
    );

    //todo добавить администраторам изменять пароль (не забыть добавить опцию которая спрашивает должны ли все ввести пароль заново и если да, то обнулить список доверенных людей)
}
