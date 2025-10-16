package van.karm.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordData(
        @Size(min = 8, max = 100, message = "Old password must be at least 8 characters")
        String oldPassword,

        @Size(min = 8, max = 100, message = "New password must be at least 8 characters")
        @NotBlank(message = "New password must be provided")
        String newPassword
) {
}
