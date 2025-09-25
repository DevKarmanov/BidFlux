package van.karm.auction.infrastructure.sanitizer;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class AuctionFieldSanitizerImpl implements AuctionFieldSanitizer {

    @Override
    public void sanitize(Map<String, Object> fieldsMap, Set<String> requestedFields) {
        boolean requestedFieldsNotEmpty = requestedFields != null && !requestedFields.isEmpty();

        fieldsMap.keySet().removeIf(key ->
                key.equals("accessCodeHash") ||
                        (requestedFieldsNotEmpty && key.equals("isPrivate") && !requestedFields.contains("isPrivate"))
        );

        if (requestedFieldsNotEmpty && requestedFields.contains("ownerName")) {
            fieldsMap.remove("ownerId");
        }
    }

    @Override
    public void sanitize(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        boolean requestedFieldsNotEmpty = requestedFields != null && !requestedFields.isEmpty();

        fieldsMap.getContent().forEach(map -> {
            Boolean isPrivate = (Boolean) map.get("isPrivate");

            if (Boolean.TRUE.equals(isPrivate)) {
                removeAll(map, "ownerId", "bidIncrement", "reservePrice", "status", "startDate", "endDate");
            }

            if (requestedFieldsNotEmpty) {
                if (!requestedFields.contains("isPrivate")) {
                    map.remove("isPrivate");
                }
                if (!requestedFields.contains("id")) {
                    map.remove("id");
                }
                if(!requestedFields.contains("ownerId")) {
                    map.remove("ownerId");
                }
                if (Boolean.TRUE.equals(isPrivate)) {
                    if (requestedFields.contains("ownerName")) {
                        removeAll(map, "ownerName");
                    }
                    if (requestedFields.contains("allowedUsersCount")) {
                        map.remove("allowedUsersCount");
                    }
                }
            }
        });
    }

    private void removeAll(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            map.remove(key);
        }
    }




}
