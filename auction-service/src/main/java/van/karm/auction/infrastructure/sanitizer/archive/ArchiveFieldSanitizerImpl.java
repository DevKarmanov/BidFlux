package van.karm.auction.infrastructure.sanitizer.archive;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.auction.application.sanitizer.FieldSanitizer;

import java.util.Map;
import java.util.Set;

@Qualifier("archive-field-sanitizer")
@Component
public class ArchiveFieldSanitizerImpl implements FieldSanitizer {
    @Override
    public void sanitize(Map<String, Object> fieldsMap, Set<String> requestedFields) {
        if (requestedFields != null && !requestedFields.isEmpty()) {
            if (!requestedFields.contains("winnerId")){
                fieldsMap.remove("winnerId");
            }
            if (!requestedFields.contains("ownerId")){
                fieldsMap.remove("ownerId");
            }
        }
    }

}
