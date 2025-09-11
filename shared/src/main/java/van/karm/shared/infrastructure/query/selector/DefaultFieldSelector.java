package van.karm.shared.infrastructure.query.selector;

import van.karm.shared.application.rule.FieldRule;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultFieldSelector implements FieldSelector{
    @Override
    public Set<String> selectFields(Set<String> requestedFields, Set<String> allowedFields, FieldRule rule) {
        Set<String> fields = (requestedFields == null || requestedFields.isEmpty())
                ? new LinkedHashSet<>(allowedFields)
                : requestedFields.stream()
                .filter(allowedFields::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        rule.apply(fields,requestedFields);
        return fields;
    }
}
