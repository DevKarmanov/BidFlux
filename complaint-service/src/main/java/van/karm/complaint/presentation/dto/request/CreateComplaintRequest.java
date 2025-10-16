package van.karm.complaint.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import van.karm.complaint.domain.model.ComplaintReason;
import van.karm.complaint.domain.model.ComplaintTargetType;

import java.util.UUID;

public record CreateComplaintRequest(
        @NotNull UUID targetId,
        @NotNull ComplaintTargetType targetType,
        @NotNull ComplaintReason reason,
        @Size(max = 500) String description
) { }
