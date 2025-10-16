package van.karm.complaint.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record ComplaintResolveRequest(
        @NotNull
        ComplaintResolutionAction action,

        @NotNull(message = "The moderator's comment should not be empty")
        String moderatorComment
) {}