package van.karm.shared.application.rule;

import java.util.Set;

public interface FieldRule {
    void apply(Set<String> filteredRequestedFields, Set<String> originalRequestedFields);
}
