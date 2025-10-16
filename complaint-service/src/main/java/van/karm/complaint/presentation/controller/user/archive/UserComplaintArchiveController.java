package van.karm.complaint.presentation.controller.user.archive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/user/complaint/archive")
@Validated
public interface UserComplaintArchiveController {

    @GetMapping
    ResponseEntity<PagedResponse> getMyArchivedComplaints(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) ComplaintTargetType targetType,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @GetMapping("/{complaintId}")
    ResponseEntity<DynamicResponse> getMyArchivedComplaint(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID complaintId,
            @RequestParam(required = false) Set<String> fields
    );
}