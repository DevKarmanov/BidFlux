package van.karm.complaint.presentation.controller.user.complaint;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/user/complaint")
@Validated
public interface UserComplaintController {

    @GetMapping
    ResponseEntity<PagedResponse> getMyComplaints(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @GetMapping("/{complaintId}")
    ResponseEntity<DynamicResponse> getMyComplaint(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID complaintId,
            @RequestParam(required = false) Set<String> fields
    );
}
