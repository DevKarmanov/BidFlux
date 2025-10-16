package van.karm.complaint.presentation.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.presentation.dto.request.ComplaintResolveRequest;
import van.karm.complaint.presentation.dto.request.CreateComplaintRequest;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RequestMapping("/complaint")
@Validated
public interface ComplaintController {

    @PostMapping
    ResponseEntity<Void> create(
            @AuthenticationPrincipal Jwt jwt,
            @Validated @RequestBody CreateComplaintRequest complaintRequest
    );

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/{complaintId}")
    ResponseEntity<DynamicResponse> getComplaint(
            @PathVariable UUID complaintId,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/from/{username}")
    ResponseEntity<PagedResponse> getFromUser(
            @PathVariable String username,
            @RequestParam(required = false) ComplaintTargetType targetType,
            @RequestParam(required = false,defaultValue = "5") int size,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")//выдает все жалобы на пользователя/аукцион
    @GetMapping("/about/{targetId}") //в зависимости от того что ищем это либо userId, либо auctionId
    ResponseEntity<PagedResponse> getAbout(
            @PathVariable UUID targetId,
            @RequestParam ComplaintTargetType targetType,
            @RequestParam(required = false,defaultValue = "5") int size,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    ); //указывать тип того что ищем (пользователь/аукцион)

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')") //выдает все жалобы вообще (можно ограничить только по типу)
    @GetMapping
    ResponseEntity<PagedResponse> getComplaints(
            @RequestParam(required = false) ComplaintTargetType targetType,
            @RequestParam(required = false,defaultValue = "5") int size,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PostMapping("/{complaintId}/resolve")
    ResponseEntity<Void> resolveComplaint(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID complaintId,
            @RequestBody @Valid ComplaintResolveRequest request
    );

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{complaintId}")
    ResponseEntity<Void> deleteComplaint(
            @PathVariable UUID complaintId
    );
}
