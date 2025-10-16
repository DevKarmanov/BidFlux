package van.karm.auth.infrastructure.provider;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.shared.application.provider.AllowedFieldsProvider;

import java.util.Set;

@Component
@Qualifier("user")
public class UserAllowedFieldsProvider implements AllowedFieldsProvider {
    private static final Set<String> ALLOWED_USER_FIELDS = Set.of(
            "id", "username","firstName","lastName","email","deleted","enabled"
    );

    @Override
    public Set<String> getAllowedFields() {
        return ALLOWED_USER_FIELDS;
    }
}
