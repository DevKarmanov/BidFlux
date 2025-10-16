package van.karm.complaint.presentation.controller.user.archive;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import van.karm.complaint.application.service.archive.ComplaintArchiveService;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserComplaintArchiveControllerImpl implements UserComplaintArchiveController {
    private final ComplaintArchiveService archiveService;

    @Override
    public ResponseEntity<PagedResponse> getMyArchivedComplaints(Jwt jwt, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getMyArchivedComplaints(jwt, targetType, size, page, fields));
    }

    @Override
    public ResponseEntity<DynamicResponse> getMyArchivedComplaint(Jwt jwt, UUID complaintId, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getMyArchivedComplaint(jwt, complaintId, fields));
    }
}
