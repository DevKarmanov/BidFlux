package van.karm.auction.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String newPassword,
        Boolean resetAllowedUsers
) {
        public ChangePasswordRequest {
                if (resetAllowedUsers == null) {
                        resetAllowedUsers = false;
                }
        }
}

