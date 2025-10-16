package van.karm.auth.infrastructure.validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Qualifier("role-name-validator")
public class RoleNameValidator implements Validator {

    private static final Pattern VALID_ROLE_PATTERN = Pattern.compile("^[A-Z0-9_]+$");
    private final String errorMessage;

    public RoleNameValidator() {
        this("Value may only contain letters, digits, and underscores");
    }

    public RoleNameValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(String input) {
        String roleNameWithoutPrefix = input.substring("ROLE_".length());
        if (!VALID_ROLE_PATTERN.matcher(roleNameWithoutPrefix).matches()) {
            throw new IllegalArgumentException("Invalid input: " + input + ". " + errorMessage);
        }
    }
}