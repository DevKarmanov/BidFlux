package van.karm.complaint.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import van.karm.complaint.application.service.ComplaintService;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.presentation.dto.request.ComplaintResolveRequest;
import van.karm.complaint.presentation.dto.request.CreateComplaintRequest;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ComplaintControllerImpl implements ComplaintController {
    private final ComplaintService complaintService;

    @Override
    public ResponseEntity<Void> create(Jwt jwt,CreateComplaintRequest complaintRequest) {
        complaintService.create(jwt,complaintRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public ResponseEntity<DynamicResponse> getComplaint(UUID complaintId, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(complaintService.getComplaint(complaintId, fields));
    }

    @Override
    public ResponseEntity<PagedResponse> getFromUser(String username, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(complaintService.getFromUser(username, targetType, size, page, fields));
    }

    @Override
    public ResponseEntity<PagedResponse> getAbout(UUID targetId, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(complaintService.getAbout(targetId, targetType, size, page, fields));
    }

    @Override
    public ResponseEntity<PagedResponse> getComplaints(ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(complaintService.getComplaints(targetType, size, page, fields));
    }

    @Override
    public ResponseEntity<Void> resolveComplaint(Jwt jwt, UUID complaintId, ComplaintResolveRequest request) {
        complaintService.resolveComplaint(jwt, complaintId, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<Void> deleteComplaint(UUID complaintId) {
        complaintService.deleteComplaint(complaintId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
