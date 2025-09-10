package van.karm.auction.infrastructure.enricher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.infrastructure.grpc.client.UserClient;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AuctionFieldEnricherImpl implements AuctionFieldEnricher {
    private final AuctionRepo auctionRepo;
    private final UserClient userClient;

    @Override
    public void enrich(Map<String, Object> fieldsMap, Boolean isPrivate, Set<String> requestedFields, UUID auctionId) {
        if (requestedFields != null && !requestedFields.isEmpty()) {
            boolean needAllowedUsersCount = requestedFields.contains("allowedUsersCount");
            boolean needOwnerName = requestedFields.contains("ownerName");

            if (Boolean.TRUE.equals(isPrivate) && needAllowedUsersCount) {
                fieldsMap.put("allowedUsersCount", auctionRepo.countAllowedUsersById(auctionId));
            }
            if (needOwnerName) {
                var ownerId = (UUID) fieldsMap.get("ownerId");
                String ownerName = userClient.getUsername(ownerId);
                if (ownerName != null) {
                    fieldsMap.put("ownerName", ownerName);
                }else throw new NullPointerException("For unknown reasons, username has become null");
            }
        }
    }
}
