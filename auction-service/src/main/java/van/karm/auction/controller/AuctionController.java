package van.karm.auction.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.AuctionInfo;
import van.karm.auction.dto.response.CreatedAuction;

import java.util.UUID;

@RequestMapping("/auction")
public interface AuctionController {

    @PostMapping
    ResponseEntity<CreatedAuction> createAuction(@Valid @RequestBody CreateAuction auctionInfo);

    @PostMapping("/{id}")
    ResponseEntity<AuctionInfo> auctionInfo(@PathVariable UUID id, @RequestParam(required = false) String password);

    //todo добавить администраторам изменять пароль (не забыть добавить опцию которая спрашивает должны ли все ввести пароль заново и если да, то обнулить список доверенных людей)
}
