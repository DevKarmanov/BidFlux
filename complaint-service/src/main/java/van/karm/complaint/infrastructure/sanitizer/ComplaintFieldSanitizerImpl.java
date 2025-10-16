package van.karm.complaint.infrastructure.sanitizer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.complaint.application.sanitizer.FieldSanitizer;

import java.util.Map;
import java.util.Set;

@Qualifier("complaint-field-sanitizer")
@Component
public class ComplaintFieldSanitizerImpl implements FieldSanitizer {
    @Override
    public void sanitize(Map<String, Object> fieldsMap, Set<String> requestedFields) {
        if (requestedFields != null && !requestedFields.isEmpty()) {
            if (!requestedFields.contains("authorId")) {
                fieldsMap.remove("authorId");
            }
            if (!requestedFields.contains("targetId")) {
                fieldsMap.remove("targetId");
            }
            if (!requestedFields.contains("targetType")) {
                fieldsMap.remove("targetType");
            }
        }
    }
}
