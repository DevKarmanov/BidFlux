package van.karm.complaint.infrastructure.validator.complaint;

import van.karm.complaint.domain.model.ComplaintTargetType;

import java.util.UUID;

public interface  ComplaintTargetValidator {
    boolean validateTarget(ComplaintTargetType targetType, UUID targetId);
    boolean validateComplaintExistence(UUID authorId, UUID targetId, ComplaintTargetType targetType);
}
