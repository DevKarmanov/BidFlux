package van.karm.auction.infrastructure.query.rule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import van.karm.shared.application.rule.FieldRule;

import java.util.Set;

@Component
@Qualifier("archive-field-rule")
public class ArchiveFieldRule implements FieldRule {
    @Override
    public void apply(Set<String> filteredRequestedFields, Set<String> originalRequestedFields) {
        filteredRequestedFields.add("winnerId");
        filteredRequestedFields.add("ownerId");
    }
}
