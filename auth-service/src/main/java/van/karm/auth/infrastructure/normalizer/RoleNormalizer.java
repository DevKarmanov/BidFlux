package van.karm.auth.infrastructure.normalizer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.auth.infrastructure.validator.Validator;

import java.util.Set;
import java.util.stream.Collectors;

@Component

public class RoleNormalizer implements Normalizer{
    private final Validator validator;

    public RoleNormalizer(@Qualifier("role-name-validator") Validator validator) {
        this.validator = validator;
    }

    @Override
    public Set<String> normalize(Set<String> roles) {
        return roles.stream()
                .filter(r -> r != null && !r.isBlank())
                .map(String::trim)
                .map(String::toUpperCase)
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .peek(validator::validate)
                .collect(Collectors.toSet());
    }
}
