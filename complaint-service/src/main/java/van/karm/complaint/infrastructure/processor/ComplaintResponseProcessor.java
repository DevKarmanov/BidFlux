package van.karm.complaint.infrastructure.processor;

import org.springframework.data.domain.Page;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Map;
import java.util.Set;

public interface ComplaintResponseProcessor {
    DynamicResponse process(Map<String, Object> data, Set<String> fields);
    PagedResponse process(Page<Map<String, Object>> paged, Set<String> fields);
}
