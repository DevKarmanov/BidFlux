package van.karm.auction.infrastructure.query.rule;

import org.springframework.stereotype.Component;
import van.karm.shared.application.rule.FieldRule;

import java.util.Set;

@Component
public class AuctionFieldRule implements FieldRule {
    @Override
    public void apply(Set<String> filteredRequestedFields, Set<String> originalRequestedFields) {
        filteredRequestedFields.add("isPrivate");
        filteredRequestedFields.add("accessCodeHash");
        if (originalRequestedFields!=null && !originalRequestedFields.isEmpty()) {
            if (originalRequestedFields.contains("ownerName")){
                filteredRequestedFields.add("ownerId");
            }
        }

    }
}
