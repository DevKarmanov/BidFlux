package van.karm.complaint.application.service.archive;

import org.springframework.security.oauth2.jwt.Jwt;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

public interface ComplaintArchiveService {
    DynamicResponse getArchivedComplaint(UUID complaintId, Set<String> fields);
    PagedResponse getArchivedFromUser(String username, ComplaintTargetType targetType, int size, int page, Set<String> fields);
    PagedResponse getArchivedAbout(UUID targetId, ComplaintTargetType targetType, int size, int page, Set<String> fields);
    PagedResponse getArchivedComplaints(ComplaintTargetType targetType, int size, int page, Set<String> fields);
    void deleteArchivedComplaint(UUID complaintId);
    PagedResponse getMyArchivedComplaints(Jwt jwt,ComplaintTargetType targetType, int size, int page, Set<String> fields);
    DynamicResponse getMyArchivedComplaint(Jwt jwt, UUID complaintId, Set<String> fields);
}
