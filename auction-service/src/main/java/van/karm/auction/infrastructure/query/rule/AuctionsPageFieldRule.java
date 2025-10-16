package van.karm.auction.infrastructure.query.rule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.shared.application.rule.FieldRule;

import java.util.Set;

@Component
@Qualifier("auction-page-field-rule")
public class AuctionsPageFieldRule implements FieldRule {
    @Override
    public void apply(Set<String> filteredRequestedFields, Set<String> originalRequestedFields) {
        filteredRequestedFields.add("isPrivate");
        filteredRequestedFields.add("id");
        if (originalRequestedFields!=null && !originalRequestedFields.isEmpty()) {
            if (originalRequestedFields.contains("ownerName")){
                filteredRequestedFields.add("ownerId");
            }
        }

    }
}
