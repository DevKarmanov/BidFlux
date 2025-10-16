package van.karm.auction.infrastructure.sanitizer.auction;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import van.karm.auction.application.sanitizer.PageFieldSanitizer;

import java.util.Map;
import java.util.Set;

@Qualifier("auction-page-field-sanitizer")
@Component
public class AuctionPageFieldSanitizerImpl implements PageFieldSanitizer {
    @Override
    public void sanitize(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        boolean requestedFieldsNotEmpty = requestedFields != null && !requestedFields.isEmpty();

        fieldsMap.getContent().forEach(map -> {
            Boolean isPrivate = (Boolean) map.get("isPrivate");

            if (Boolean.TRUE.equals(isPrivate)) {
                removeAll(map, "ownerId", "bidIncrement", "reservePrice", "status", "startDate", "endDate","description");
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
