package van.karm.auction.infrastructure.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.application.enricher.FieldEnricher;
import van.karm.auction.application.enricher.PageFieldEnricher;
import van.karm.auction.application.sanitizer.FieldSanitizer;
import van.karm.auction.application.sanitizer.PageFieldSanitizer;
import van.karm.auction.application.service.ArchiveService;
import van.karm.auction.domain.model.Archive;
import van.karm.auction.domain.repo.ArchiveRepo;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;
import van.karm.auction.presentation.exception.AccessDeniedException;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.QueryExecutor;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ArchiveServiceImpl implements ArchiveService {
    private final ArchiveRepo archiveRepo;
    private final FieldEnricher archiveFieldEnricher;
    private final PageFieldEnricher archivePageFieldEnricher;
    private final FieldSanitizer archiveFieldSanitizer;
    private final PageFieldSanitizer archivePageFieldSanitizer;
    private final AllowedFieldsProvider archiveFieldsProvider;
    private final AllowedFieldsProvider archivePageFieldsProvider;
    private final FieldRule archiveFieldRule;
    private final QueryExecutor queryExecutor;
    private final TransactionTemplate transactionTemplate;

    public ArchiveServiceImpl(ArchiveRepo archiveRepo,
                              FieldEnricher archiveFieldEnricher,
                              PageFieldEnricher archivePageFieldEnricher,
                              @Qualifier("archive-field-sanitizer") FieldSanitizer archiveFieldSanitizer,
                              @Qualifier("archive-page-field-sanitizer") PageFieldSanitizer archivePageFieldSanitizer,
                              @Qualifier("archive-allowed-fields") AllowedFieldsProvider archiveFieldsProvider,
                              @Qualifier("archive-page-allowed-fields") AllowedFieldsProvider archivePageFieldsProvider,
                              FieldRule archiveFieldRule,
                              QueryExecutor queryExecutor,
                              TransactionTemplate transactionTemplate) {
        this.archiveRepo = archiveRepo;
        this.archiveFieldEnricher = archiveFieldEnricher;
        this.archivePageFieldEnricher = archivePageFieldEnricher;
        this.archiveFieldSanitizer = archiveFieldSanitizer;
        this.archivePageFieldSanitizer = archivePageFieldSanitizer;
        this.archiveFieldsProvider = archiveFieldsProvider;
        this.archivePageFieldsProvider = archivePageFieldsProvider;
        this.archiveFieldRule = archiveFieldRule;
        this.queryExecutor = queryExecutor;
        this.transactionTemplate = transactionTemplate;
    }

    private PagedResponse getPageOfArchivedAuctions(UUID userId, Set<String> fields, int size, int page){
        Pageable pageable = PageRequest.of(page, size);

        FieldRule rule = (filtered, original)->{
            if (original!=null && !original.isEmpty()){
                if (original.contains("winnerName")){
                    filtered.add("winnerId");
                }
                if (original.contains("ownerName")){
                    filtered.add("ownerId");
                }
            }
        };

        var result = queryExecutor.selectQueryByFieldPaged(
                Archive.class,
                Map.of("ownerId",userId,"winnerId",userId),
                LogicalOperator.OR,
                fields,
                archivePageFieldsProvider,
                rule,
                pageable);

        archivePageFieldEnricher.enrich(result, fields);
        archivePageFieldSanitizer.sanitize(result,fields);

        return new PagedResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast(),
                result.getNumberOfElements());
    }

    @Override
    public void forcedDeleteArchivedAuction(UUID id) {
        int deleted = Optional.ofNullable(transactionTemplate.execute(status->archiveRepo.deleteArchiveById(id)))
                .orElseThrow(()-> new RuntimeException("Archive deletion failed"));

        if (deleted == 0) {
            throw new EntityNotFoundException("Archived auction not found with id: " + id);
        }
    }


    @Override
    public PagedResponse getAllUserArchivedAuctions(String username, Set<String> fields, int size, int page) {
        UUID userId = archiveRepo.getUserIdByUsername(username);
        return getPageOfArchivedAuctions(userId,fields,size,page);
    }

    @Override
    public PagedResponse getMyArchivedAuctions(Jwt jwt, Set<String> fields, int size, int page) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        return getPageOfArchivedAuctions(userId,fields,size,page);
    }

    @Override
    public DynamicResponse getArchivedAuction(Jwt jwt, Set<String> fields, UUID id) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));

        var result = queryExecutor.selectQueryByField(
                Archive.class,
                Map.of("id",id),
                LogicalOperator.NONE,
                fields,
                archiveFieldsProvider,
                archiveFieldRule);

        UUID winnerId = (UUID) result.get("winnerId");
        UUID ownerId = (UUID) result.get("ownerId");

        if (!userId.equals(ownerId) && !userId.equals(winnerId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }

        archiveFieldEnricher.enrich(result, fields);
        archiveFieldSanitizer.sanitize(result,fields);
        return new DynamicResponse(result);
    }
}
