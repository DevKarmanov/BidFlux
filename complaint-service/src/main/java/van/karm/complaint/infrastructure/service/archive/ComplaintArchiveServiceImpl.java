package van.karm.complaint.infrastructure.service.archive;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.complaint.application.service.archive.ComplaintArchiveService;
import van.karm.complaint.application.service.query.EntityQueryService;
import van.karm.complaint.common.utils.MapUtils;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.domain.model.archive.ComplaintArchive;
import van.karm.complaint.domain.repo.ArchiveRepo;
import van.karm.complaint.domain.repo.ComplaintRepo;
import van.karm.complaint.infrastructure.processor.ComplaintResponseProcessor;
import van.karm.complaint.infrastructure.validator.user.Validator;
import van.karm.complaint.presentation.dto.response.DynamicResponse;
import van.karm.complaint.presentation.dto.response.PagedResponse;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComplaintArchiveServiceImpl implements ComplaintArchiveService {
    private final TransactionTemplate tx;
    private final ArchiveRepo archiveRepo;
    private final ComplaintRepo complaintRepo;
    private final ComplaintResponseProcessor responseProcessor;
    private final EntityQueryService<ComplaintArchive> query;
    private final Validator specialNameValidator;


    @Override
    public DynamicResponse getArchivedComplaint(UUID complaintId, Set<String> fields) {
        var fieldsMap = query.getSingle(Map.of("id", complaintId),LogicalOperator.NONE, fields);
        return responseProcessor.process(fieldsMap,fields);
    }

    @Override
    public PagedResponse getArchivedFromUser(String username, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        specialNameValidator.validate(username);

        UUID userId = complaintRepo.getAuthorIdByUsername(username);

        Map<String, Object> filters = MapUtils
                .ofNonNull(
                        MapUtils.entry("authorId", userId),
                        MapUtils.entry("targetType", targetType)
                );

        Pageable pageable = PageRequest.of(page, size);

        var fieldsMap = query.getPaged(filters, LogicalOperator.AND,fields,pageable);
        return responseProcessor.process(fieldsMap,fields);
    }

    @Override
    public PagedResponse getArchivedAbout(UUID targetId, ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        Map<String, Object> filters = MapUtils
                .ofNonNull(
                        MapUtils.entry("targetId", targetId),
                        MapUtils.entry("targetType", targetType)
                );

        Pageable pageable = PageRequest.of(page, size);

        var fieldsMap = query.getPaged(filters, LogicalOperator.AND,fields,pageable);
        return responseProcessor.process(fieldsMap,fields);
    }

    @Override
    public PagedResponse getArchivedComplaints(ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        Pageable pageable = PageRequest.of(page, size);

        Map<String, Object> filters = MapUtils
                .ofNonNull(
                        MapUtils.entry("targetType", targetType)
                );
        var paged = query.getPaged(filters, LogicalOperator.NONE, fields, pageable);
        return responseProcessor.process(paged, fields);
    }

    @Override
    public void deleteArchivedComplaint(UUID complaintId) {
        tx.executeWithoutResult(status -> archiveRepo.deleteById(complaintId));
    }

    @Override
    public PagedResponse getMyArchivedComplaints(Jwt jwt,ComplaintTargetType targetType, int size, int page, Set<String> fields) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));

        Map<String, Object> filters = MapUtils
                .ofNonNull(
                        MapUtils.entry("authorId", userId),
                        MapUtils.entry("targetType", targetType)
                );

        Pageable pageable = PageRequest.of(page, size);

        var fieldsMap = query.getPaged(filters, LogicalOperator.AND,fields,pageable);
        return responseProcessor.process(fieldsMap,fields);
    }

    @Override
    public DynamicResponse getMyArchivedComplaint(Jwt jwt, UUID complaintId, Set<String> fields) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));

        var fieldsMap = query.getSingle(Map.of("authorId", userId, "id",complaintId),LogicalOperator.AND,fields);
        return responseProcessor.process(fieldsMap, fields);
    }
}
