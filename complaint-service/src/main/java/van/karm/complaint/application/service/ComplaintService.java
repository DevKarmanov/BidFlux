package van.karm.complaint.application.service;

import org.springframework.security.oauth2.jwt.Jwt;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.presentation.dto.request.ComplaintResolveRequest;
import van.karm.complaint.presentation.dto.request.CreateComplaintRequest;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

public interface ComplaintService {
    void create(Jwt jwt, CreateComplaintRequest complaintRequest);
    DynamicResponse getComplaint(UUID complaintId, Set<String> fields);
    PagedResponse getFromUser(String username, ComplaintTargetType targetType, int size, int page, Set<String> fields);
    PagedResponse getAbout(UUID targetId, ComplaintTargetType targetType, int size, int page, Set<String> fields);
    PagedResponse getComplaints(ComplaintTargetType targetType, int size, int page, Set<String> fields);
    void resolveComplaint(Jwt jwt,UUID complaintId, ComplaintResolveRequest request);
    PagedResponse getMyComplaints(Jwt jwt, int size, int page, Set<String> fields);
    DynamicResponse getMyComplaint(Jwt jwt, UUID complaintId, Set<String> fields);
    void deleteComplaint(UUID complaintId);
}
