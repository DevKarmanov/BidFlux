package van.karm.auth.presentation.dto.request;

import jakarta.validation.constraints.*;

public record UserRegData(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can contain only letters, numbers, and underscores")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "First name can contain only letters")
        String firstName,

        @Size(max = 50, message = "Last name must be at most 50 characters")
        @Pattern(regexp = "^[a-zA-Z]*$", message = "Last name can contain only letters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email
) {}
