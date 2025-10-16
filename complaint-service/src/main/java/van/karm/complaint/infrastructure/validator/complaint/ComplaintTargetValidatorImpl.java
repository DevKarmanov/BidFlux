package van.karm.complaint.infrastructure.validator.complaint;

import org.springframework.stereotype.Service;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.domain.repo.ComplaintRepo;

import java.util.UUID;

@Service
public class ComplaintTargetValidatorImpl implements ComplaintTargetValidator {
    private final ComplaintRepo complaintRepo;

    public ComplaintTargetValidatorImpl(ComplaintRepo complaintRepo) {
        this.complaintRepo = complaintRepo;
    }

    @Override
    public boolean validateTarget(ComplaintTargetType targetType, UUID targetId) {
        return switch (targetType) {
            case AUCTION -> complaintRepo.existsAuctionById(targetId);
            case USER -> complaintRepo.existsUserById(targetId);
        };
    }

    @Override
    public boolean validateComplaintExistence(UUID authorId, UUID targetId, ComplaintTargetType targetType) {
        return complaintRepo.existsByAuthorIdAndTargetIdAndTargetType(authorId, targetId, targetType);
    }
}
