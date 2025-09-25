package van.karm.bid.infrastructure.rule;

import org.springframework.stereotype.Component;
import van.karm.shared.application.rule.FieldRule;

import java.util.Set;

@Component
public class BidFieldRule implements FieldRule {
    @Override
    public void apply(Set<String> filteredRequestedFields, Set<String> originalRequestedFields) {
        if (originalRequestedFields!=null && !originalRequestedFields.isEmpty()) {
            if (originalRequestedFields.contains("bidOwnerName")){
                filteredRequestedFields.add("userId");
            }
        }

    }
}
