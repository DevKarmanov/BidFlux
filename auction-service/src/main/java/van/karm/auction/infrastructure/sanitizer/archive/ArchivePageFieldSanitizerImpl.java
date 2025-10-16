package van.karm.auction.infrastructure.sanitizer.archive;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import van.karm.auction.application.sanitizer.PageFieldSanitizer;

import java.util.Map;
import java.util.Set;

@Qualifier("archive-page-field-sanitizer")
@Component
public class ArchivePageFieldSanitizerImpl implements PageFieldSanitizer {
    @Override
    public void sanitize(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        boolean requestedFieldsNotEmpty = requestedFields != null && !requestedFields.isEmpty();

        fieldsMap.getContent().forEach(map -> {
            if (requestedFieldsNotEmpty) {
                if (!requestedFields.contains("winnerId")) {
                    map.remove("winnerId");
                }
                if (!requestedFields.contains("ownerId")) {
                    map.remove("ownerId");
                }
            }
        });
    }
}
