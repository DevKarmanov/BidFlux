package van.karm.auction.infrastructure.enricher.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.auction.application.enricher.FieldEnricher;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.infrastructure.grpc.client.user.UserClient;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@Qualifier("auction-field-enricher")
@RequiredArgsConstructor
public class AuctionFieldEnricherImpl implements FieldEnricher {
    private final AuctionRepo auctionRepo;
    private final UserClient userClient;

    @Override
    public void enrich(Map<String, Object> fieldsMap, Set<String> requestedFields) {
        if (requestedFields == null || requestedFields.isEmpty()) {
            return;
        }

        boolean needAllowedUsersCount = requestedFields.contains("allowedUsersCount");
        boolean needOwnerName = requestedFields.contains("ownerName");

        if (needAllowedUsersCount){
            boolean isPrivate = (Boolean) fieldsMap.get("isPrivate");
            if (isPrivate){
                UUID auctionId = (UUID) fieldsMap.get("id");
                fieldsMap.put("allowedUsersCount", auctionRepo.countAllowedUsersById(auctionId));

            }
        }

        if (needOwnerName) {
            UUID ownerId = (UUID) fieldsMap.get("ownerId");
            String ownerName = userClient.getUsername(ownerId);

            if (ownerName != null) {
                fieldsMap.put("ownerName", ownerName);
            } else {
                throw new IllegalStateException("Username is null for ownerId=" + ownerId);
            }
        }
    }
}

