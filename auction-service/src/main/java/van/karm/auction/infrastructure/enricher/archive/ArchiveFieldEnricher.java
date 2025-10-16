package van.karm.auction.infrastructure.enricher.archive;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.auction.application.enricher.FieldEnricher;
import van.karm.auction.domain.repo.ArchiveRepo;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@Qualifier("archive-field-enricher")
@RequiredArgsConstructor
public class ArchiveFieldEnricher implements FieldEnricher {
    private final ArchiveRepo archiveRepo;

    @Override
    public void enrich(Map<String, Object> fieldsMap, Set<String> requestedFields) {
        if (requestedFields == null || requestedFields.isEmpty()) {
            return;
        }

        boolean needWinnerName = requestedFields.contains("winnerName");
        boolean needOwnerName = requestedFields.contains("ownerName");

        System.out.println(fieldsMap);

        if (needWinnerName){
            UUID winnerId = (UUID) fieldsMap.get("winnerId");
            String winnerName = archiveRepo.getUserNameByUserId(winnerId);
            fieldsMap.put("winnerName", winnerName);
        }

        if (needOwnerName){
            UUID winnerId = (UUID) fieldsMap.get("ownerId");
            String ownerName = archiveRepo.getUserNameByUserId(winnerId);
            fieldsMap.put("ownerName", ownerName);
        }
    }
}
