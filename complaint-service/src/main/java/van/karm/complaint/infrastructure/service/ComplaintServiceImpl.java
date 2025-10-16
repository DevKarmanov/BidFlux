package van.karm.complaint.infrastructure.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.complaint.application.service.ComplaintService;
import van.karm.complaint.application.service.query.EntityQueryService;
import van.karm.complaint.common.utils.MapUtils;
import van.karm.complaint.domain.model.ComplaintStatus;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.domain.model.archive.ComplaintArchive;
import van.karm.complaint.domain.model.complaint.Complaint;
import van.karm.complaint.domain.repo.ArchiveRepo;
import van.karm.complaint.domain.repo.ComplaintRepo;
import van.karm.complaint.infrastructure.processor.ComplaintResponseProcessor;
import van.karm.complaint.infrastructure.validator.complaint.ComplaintTargetValidator;
import van.karm.complaint.infrastructure.validator.user.Validator;
import van.karm.complaint.presentation.dto.request.ComplaintResolveRequest;
import van.karm.complaint.presentation.dto.request.CreateComplaintRequest;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;

import java.util.*;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final TransactionTemplate tx;
    private final ComplaintRepo complaintRepo;
    private final ArchiveRepo archiveRepo;
    private final ComplaintTargetValidator targetValidator;
    private final EntityQueryService<Complaint> query;
    private final ComplaintResponseProcessor responseProcessor;
    private final Validator specialNameValidator;

    public ComplaintServiceImpl(
            TransactionTemplate tx,
            ComplaintRepo complaintRepo, ArchiveRepo archiveRepo,
            ComplaintTargetValidator targetValidator,
            EntityQueryService<Complaint> query,
            ComplaintResponseProcessor responseProcessor, Validator specialNameValidator) {
        this.tx = tx;
        this.complaintRepo = complaintRepo;
        this.archiveRepo = archiveRepo;
        this.targetValidator = targetValidator;
        this.query = query;
        this.responseProcessor = responseProcessor;
        this.specialNameValidator = specialNameValidator;
    }

    @Override
    public void create(Jwt jwt, CreateComplaintRequest req) {
        UUID authorId = UUID.fromString(jwt.getClaim("userId"));
        UUID targetId = req.targetId();
        ComplaintTargetType targetType = req.targetType();

        if (targetType.equals(ComplaintTargetType.USER) && targetId.equals(authorId)){
            throw new IllegalArgumentException("Why are you complaining about yourself?");
        }

        if (targetValidator.validateComplaintExistence(authorId,targetId,targetType)){
            throw new EntityExistsException("Complaint already exists");
        }

        if (!targetValidator.validateTarget(targetType, targetId)) {
            throw new IllegalArgumentException("Target entity not found");
        }

        String description = Optional.ofNullable(req.description())
                .map(String::trim)
                .orElse(null);

        Complaint complaint = new Complaint(authorId, targetType, targetId, req.reason(), description);
        tx.executeWithoutResult(status -> complaintRepo.save(complaint));
    }

    @Override
    public DynamicResponse getComplaint(UUID complaintId, Set<String> fields) {
        var fieldsMap = query.getSingle(Map.of("id", complaintId),LogicalOperator.NONE, fields);
        return responseProcessor.process(fieldsMap, fields);
    }

    @Override
    public PagedResponse getFromUser(String username, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        specialNameValidator.validate(username);

        Pageable pageable = PageRequest.of(page, size);
        UUID userId = complaintRepo.getAuthorIdByUsername(username);


        Map<String, Object> filters = MapUtils
                .ofNonNull(
                        MapUtils.entry("authorId", userId),
                        MapUtils.entry("targetType", targetType)
                );
        var paged = query.getPaged(filters, LogicalOperator.AND, fields, pageable);
        return responseProcessor.process(paged, fields);
    }

    @Override
    public PagedResponse getAbout(UUID targetId, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        Pageable pageable = PageRequest.of(page, size);
        var paged = query.getPaged(Map.of("targetId", targetId, "targetType", targetType), LogicalOperator.AND, fields, pageable);
        return responseProcessor.process(paged, fields);
    }

    @Override
    public PagedResponse getComplaints(ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        Pageable pageable = PageRequest.of(page, size);

        Map<String, Object> filters = MapUtils
                .ofNonNull(
                        MapUtils.entry("targetType", targetType)
                );
        var paged = query.getPaged(filters, LogicalOperator.NONE, fields, pageable);
        return responseProcessor.process(paged, fields);
    }

    @Override
    public PagedResponse getMyComplaints(Jwt jwt, int size, int page, Set<String> fields) {
        UUID authorId = UUID.fromString(jwt.getClaim("userId"));
        Pageable pageable = PageRequest.of(page, size);
        var paged = query.getPaged(Map.of("authorId", authorId), LogicalOperator.NONE, fields, pageable);
        return responseProcessor.process(paged, fields);
    }

    @Override
    public DynamicResponse getMyComplaint(Jwt jwt, UUID complaintId, Set<String> fields) {
        UUID authorId = UUID.fromString(jwt.getClaim("userId"));
        var fieldsMap = query.getSingle(Map.of("authorId", authorId), LogicalOperator.NONE, fields);
        return responseProcessor.process(fieldsMap, fields);
    }

    @Override
    public void deleteComplaint(UUID complaintId) {
        tx.executeWithoutResult(status -> complaintRepo.deleteById(complaintId));
    }

    @Override
    public void resolveComplaint(Jwt jwt, UUID complaintId, ComplaintResolveRequest request) {
        UUID authorId = UUID.fromString(jwt.getClaim("userId"));
        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(()-> new EntityNotFoundException("Complaint with id " + complaintId + " not found"));

        ComplaintStatus newStatus = switch (request.action()) {
            case ACCEPT -> ComplaintStatus.RESOLVED;
            case REJECT -> ComplaintStatus.REJECTED;
        };

        ComplaintArchive archive = new ComplaintArchive(complaint,request.moderatorComment(),authorId,newStatus);
        tx.executeWithoutResult(status -> {
            archiveRepo.save(archive);
            complaintRepo.delete(complaint);
        });

    }
}

