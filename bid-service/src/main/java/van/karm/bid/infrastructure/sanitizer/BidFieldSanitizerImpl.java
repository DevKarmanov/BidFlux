package van.karm.bid.infrastructure.sanitizer;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class BidFieldSanitizerImpl implements BidFieldSanitizer {

    @Override
    public void sanitize(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        boolean requestedFieldsNotEmpty = requestedFields != null && !requestedFields.isEmpty();

        if (requestedFieldsNotEmpty && requestedFields.contains("bidOwnerName")) {
            fieldsMap.getContent().forEach(map -> map.remove("userId"));
        }
    }

}
