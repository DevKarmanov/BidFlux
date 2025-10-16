package van.karm.auction.infrastructure.sanitizer.auction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.auction.application.sanitizer.FieldSanitizer;

import java.util.Map;
import java.util.Set;

@Qualifier("auction-field-sanitizer")
@Component
public class AuctionFieldSanitizerImpl implements FieldSanitizer {

    @Override
    public void sanitize(Map<String, Object> fieldsMap, Set<String> requestedFields) {
        boolean requestedFieldsNotEmpty = requestedFields != null && !requestedFields.isEmpty();

        fieldsMap.keySet().removeIf(key ->
                key.equals("accessCodeHash") ||
                        (requestedFieldsNotEmpty && key.equals("isPrivate") && !requestedFields.contains("isPrivate"))
        );

        if (requestedFieldsNotEmpty && !requestedFields.contains("id")) {
            fieldsMap.remove("id");
        }

        if (requestedFieldsNotEmpty && requestedFields.contains("ownerName")) {
            fieldsMap.remove("ownerId");
        }
    }
}
