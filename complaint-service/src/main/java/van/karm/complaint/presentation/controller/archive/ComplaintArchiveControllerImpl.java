package van.karm.complaint.presentation.controller.archive;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import van.karm.complaint.application.service.archive.ComplaintArchiveService;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ComplaintArchiveControllerImpl implements ComplaintArchiveController {
    private final ComplaintArchiveService archiveService;

    @Override
    public ResponseEntity<DynamicResponse> getArchivedComplaint(UUID complaintId, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getArchivedComplaint(complaintId, fields));
    }

    @Override
    public ResponseEntity<PagedResponse> getArchivedFromUser(String username, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getArchivedFromUser(username, targetType, size, page, fields));
    }

    @Override
    public ResponseEntity<PagedResponse> getArchivedAbout(UUID targetId, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getArchivedAbout(targetId, targetType, size, page, fields));
    }

    @Override
    public ResponseEntity<PagedResponse> getArchivedComplaints(ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getArchivedComplaints(targetType, size, page, fields));
    }

    @Override
    public ResponseEntity<Void> deleteArchivedComplaint(UUID complaintId) {
        archiveService.deleteArchivedComplaint(complaintId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
