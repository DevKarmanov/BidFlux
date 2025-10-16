package van.karm.complaint.infrastructure.enricher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.complaint.application.enricher.FieldEnricher;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.domain.repo.ComplaintRepo;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Qualifier("complaint-field-enricher")
public class ComplaintFieldEnricherImpl implements FieldEnricher {
    private final ComplaintRepo complaintRepo;

    @Override
    public void enrich(Map<String, Object> fieldsMap, Set<String> requestedFields) {
        if (requestedFields != null && !requestedFields.isEmpty()) {
            if (requestedFields.contains("authorName")) {
                var authorId = (UUID) fieldsMap.get("authorId");
                String username = complaintRepo.getUsernameByAuthorId(authorId);
                fieldsMap.put("authorName", username);
            }
            if (requestedFields.contains("targetName")) {
                var targetId = (UUID) fieldsMap.get("targetId");
                var targetType = (ComplaintTargetType) fieldsMap.get("targetType");
                String targetName;
                if (targetType.equals(ComplaintTargetType.USER)) {
                    targetName = complaintRepo.getUsernameByAuthorId(targetId);
                    fieldsMap.put("targetName", targetName);
                }else if (targetType.equals(ComplaintTargetType.AUCTION)) {
                    targetName = resolveAuctionTitle(targetId);
                    fieldsMap.put("targetName", targetName);
                }
            }
        }
    }

    private String resolveAuctionTitle(UUID targetId) {
        String title = complaintRepo.getAuctionTitleById(targetId);
        return title != null ? title : complaintRepo.getArchivedAuctionTitleById(targetId);
    }

}
