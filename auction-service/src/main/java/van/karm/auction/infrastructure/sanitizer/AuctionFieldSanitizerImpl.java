package van.karm.auction.infrastructure.sanitizer;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class AuctionFieldSanitizerImpl implements AuctionFieldSanitizer {
    @Override
    public void sanitize(Map<String, Object> fieldsMap, Set<String> requestedFields) {
        boolean requestedFieldsNotEmpty = requestedFields!=null && !requestedFields.isEmpty();

        fieldsMap.keySet().removeIf(key ->
                key.equals("accessCodeHash") ||
                        (requestedFieldsNotEmpty && key.equals("isPrivate") && !requestedFields.contains("isPrivate"))
        );

        if (requestedFieldsNotEmpty && requestedFields.contains("ownerName")) {
            fieldsMap.remove("ownerId");
        }
    }
}
