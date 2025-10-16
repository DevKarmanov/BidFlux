package van.karm.bid.presentation.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import van.karm.bid.application.service.BidService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BidWsControllerImpl implements BidWsController {

    private final BidService bidService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @MessageMapping("/last-bid")
    public void getLastBidByAuctionId(@Payload String auctionId, Principal principal) {
        auctionId = auctionId.replace("\"", "");
        UUID auctionIdUUID = UUID.fromString(auctionId);
        try {
            if (principal instanceof UsernamePasswordAuthenticationToken auth) {
                Jwt jwt = (Jwt) auth.getPrincipal();
                BigDecimal lastBid = bidService.getLastBid(auctionIdUUID, jwt);
                messagingTemplate.convertAndSend("/topic/bid/" + auctionId,
                        Map.of("status", "ok", "value", lastBid));
            }
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/bid/" + auctionId,
                    Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
