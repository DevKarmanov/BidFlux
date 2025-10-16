package van.karm.complaint.infrastructure.processor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import van.karm.complaint.application.enricher.FieldEnricher;
import van.karm.complaint.application.enricher.PageFieldEnricher;
import van.karm.complaint.application.sanitizer.FieldSanitizer;
import van.karm.complaint.application.sanitizer.PageFieldSanitizer;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Map;
import java.util.Set;

@Service
public class ComplaintResponseProcessorImpl implements ComplaintResponseProcessor {
    private final FieldEnricher fieldEnricher;
    private final FieldSanitizer fieldSanitizer;
    private final PageFieldEnricher pageFieldEnricher;
    private final PageFieldSanitizer pageFieldSanitizer;

    public ComplaintResponseProcessorImpl(
            @Qualifier("complaint-field-enricher") FieldEnricher fieldEnricher,
            @Qualifier("complaint-field-sanitizer") FieldSanitizer fieldSanitizer,
            @Qualifier("complaint-page-field-enricher") PageFieldEnricher pageFieldEnricher,
            @Qualifier("complaint-page-field-sanitizer") PageFieldSanitizer pageFieldSanitizer) {
        this.fieldEnricher = fieldEnricher;
        this.fieldSanitizer = fieldSanitizer;
        this.pageFieldEnricher = pageFieldEnricher;
        this.pageFieldSanitizer = pageFieldSanitizer;
    }

    @Override
    public DynamicResponse process(Map<String, Object> data, Set<String> fields) {
        fieldEnricher.enrich(data, fields);
        fieldSanitizer.sanitize(data, fields);
        return new DynamicResponse(data);
    }

    @Override
    public PagedResponse process(Page<Map<String, Object>> paged, Set<String> fields) {
        pageFieldEnricher.enrich(paged, fields);
        pageFieldSanitizer.sanitize(paged, fields);
        return new PagedResponse(
                paged.getContent(),
                paged.getNumber(),
                paged.getSize(),
                paged.getTotalElements(),
                paged.getTotalPages(),
                paged.isFirst(),
                paged.isLast(),
                paged.getNumberOfElements()
        );
    }
}
