package van.karm.complaint.infrastructure.rule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.shared.application.rule.FieldRule;

import java.util.Set;

@Component
@Qualifier("complaint-rule")
public class AuthorAndTargetNameRule implements FieldRule {
    @Override
    public void apply(Set<String> filteredRequestedFields, Set<String> originalRequestedFields) {
        if (originalRequestedFields!=null && !originalRequestedFields.isEmpty()) {
            if (originalRequestedFields.contains("authorName")) {
                filteredRequestedFields.add("authorId");
            }
            if (originalRequestedFields.contains("targetName")) {
                filteredRequestedFields.add("targetId");
                filteredRequestedFields.add("targetType");
            }
        }
    }
}
