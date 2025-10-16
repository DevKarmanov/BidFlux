package van.karm.complaint.infrastructure.sanitizer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import van.karm.complaint.application.sanitizer.FieldSanitizer;
import van.karm.complaint.application.sanitizer.PageFieldSanitizer;

import java.util.Map;
import java.util.Set;

@Component
@Qualifier("complaint-page-field-sanitizer")
public class ComplaintPageFieldSanitizerImpl implements PageFieldSanitizer {
    private final FieldSanitizer fieldSanitizer;

    public ComplaintPageFieldSanitizerImpl(FieldSanitizer fieldSanitizer) {
        this.fieldSanitizer = fieldSanitizer;
    }

    @Override
    public void sanitize(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        for (Map<String, Object> field : fieldsMap.getContent()) {
            fieldSanitizer.sanitize(field, requestedFields);
        }
    }
}
