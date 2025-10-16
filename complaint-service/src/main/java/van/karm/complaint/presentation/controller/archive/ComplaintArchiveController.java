package van.karm.complaint.presentation.controller.archive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/complaint/archive")
@Validated
public interface ComplaintArchiveController {

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/{complaintId}")
    ResponseEntity<DynamicResponse> getArchivedComplaint(
            @PathVariable UUID complaintId,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/from/{username}")
    ResponseEntity<PagedResponse> getArchivedFromUser(
            @PathVariable String username,
            @RequestParam(required = false) ComplaintTargetType targetType,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/about/{targetId}") // userId или auctionId
    ResponseEntity<PagedResponse> getArchivedAbout(
            @PathVariable UUID targetId,
            @RequestParam ComplaintTargetType targetType,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping
    ResponseEntity<PagedResponse> getArchivedComplaints(
            @RequestParam(required = false) ComplaintTargetType targetType,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{complaintId}")
    ResponseEntity<Void> deleteArchivedComplaint(@PathVariable UUID complaintId);
}
