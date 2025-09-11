package van.karm.auth.infrastructure.provider;

import org.springframework.stereotype.Component;
import van.karm.shared.application.provider.AllowedFieldsProvider;

import java.util.Set;

@Component
public class UserAllowedFieldsProvider implements AllowedFieldsProvider {
    private static final Set<String> ALLOWED_USER_FIELDS = Set.of(
            "id", "username"
    );

    @Override
    public Set<String> getAllowedFields() {
        return ALLOWED_USER_FIELDS;
    }
}
