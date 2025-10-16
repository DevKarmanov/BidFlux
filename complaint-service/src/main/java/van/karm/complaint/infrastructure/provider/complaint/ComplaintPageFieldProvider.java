package van.karm.complaint.infrastructure.provider.complaint;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.shared.application.provider.AllowedFieldsProvider;

import java.util.Set;

@Component
@Qualifier("complaint-page-field-provider")
public class ComplaintPageFieldProvider implements AllowedFieldsProvider {
    private static final Set<String> ALLOWED_USER_FIELDS = Set.of(
            "id", "targetType","targetId","reason","status","createdAt"
    );

    @Override
    public Set<String> getAllowedFields() {
        return ALLOWED_USER_FIELDS;
    }
}
