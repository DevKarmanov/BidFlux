package van.karm.bid.presentation.dto.response;

import java.util.List;
import java.util.Map;

public record PagedResponse(
        List<Map<String, Object>> data,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        int numberOfElements
) {
}
