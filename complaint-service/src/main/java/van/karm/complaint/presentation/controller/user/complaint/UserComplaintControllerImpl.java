package van.karm.complaint.presentation.controller.user.complaint;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import van.karm.complaint.application.service.ComplaintService;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserComplaintControllerImpl implements UserComplaintController {
    private final ComplaintService complaintService;

    @Override
    public ResponseEntity<PagedResponse> getMyComplaints(Jwt jwt, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(complaintService.getMyComplaints(jwt, size, page, fields));
    }

    @Override
    public ResponseEntity<DynamicResponse> getMyComplaint(Jwt jwt, UUID complaintId, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(complaintService.getMyComplaint(jwt, complaintId, fields));
    }
}
