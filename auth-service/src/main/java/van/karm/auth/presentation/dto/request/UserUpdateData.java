package van.karm.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateData(
        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "First name can contain only letters")
        String firstName,

        @Size(max = 50, message = "Last name must be at most 50 characters")
        @Pattern(regexp = "^[a-zA-Z]*$", message = "Last name can contain only letters")
        String lastName,

        @Email(message = "Email should be valid")
        String email) {
}
