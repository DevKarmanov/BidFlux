package van.karm.complaint.infrastructure.enricher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import van.karm.complaint.application.enricher.FieldEnricher;
import van.karm.complaint.application.enricher.PageFieldEnricher;

import java.util.Map;
import java.util.Set;

@Component
@Qualifier("complaint-page-field-enricher")
public class ComplaintPageFieldEnricherImpl implements PageFieldEnricher {
    private final FieldEnricher fieldEnricher;

    public ComplaintPageFieldEnricherImpl(FieldEnricher fieldEnricher) {
        this.fieldEnricher = fieldEnricher;
    }

    @Override
    public void enrich(Page<Map<String, Object>> fieldsMap, Set<String> requestedFields) {
        for (Map<String, Object> field : fieldsMap.getContent()) {
            fieldEnricher.enrich(field, requestedFields);
        }
    }
}
