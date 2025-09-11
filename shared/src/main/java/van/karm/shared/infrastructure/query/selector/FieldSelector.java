package van.karm.shared.infrastructure.query.selector;

import van.karm.shared.application.rule.FieldRule;

import java.util.Set;

public interface FieldSelector {
    Set<String> selectFields(Set<String> requestedFields, Set<String> allowedFields, FieldRule rule);
}
