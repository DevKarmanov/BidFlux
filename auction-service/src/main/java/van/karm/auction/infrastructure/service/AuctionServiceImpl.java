package van.karm.auction.infrastructure.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.application.enricher.FieldEnricher;
import van.karm.auction.application.enricher.PageFieldEnricher;
import van.karm.auction.application.sanitizer.FieldSanitizer;
import van.karm.auction.application.sanitizer.PageFieldSanitizer;
import van.karm.auction.application.service.AuctionService;
import van.karm.auction.application.validator.AuctionValidator;
import van.karm.auction.domain.factory.AuctionBuilder;
import van.karm.auction.domain.model.Auction;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.infrastructure.security.encode.Encoder;
import van.karm.auction.presentation.dto.request.ChangePasswordRequest;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;
import van.karm.auction.presentation.exception.AccessDeniedException;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.QueryExecutor;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;

import java.util.*;

@Component
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepo auctionRepo;
    private final TransactionTemplate transactionTemplate;
    private final QueryExecutor queryExecutor;
    private final Encoder encoder;
    private final FieldEnricher auctionFieldEnricher;
    private final PageFieldEnricher auctionPageFieldEnricher;
    private final FieldSanitizer auctionFieldSanitizer;
    private final PageFieldSanitizer auctionPageFieldSanitizer;
    private final AuctionValidator auctionValidator;
    private final AllowedFieldsProvider fullAuctionFieldsProvider;
    private final AllowedFieldsProvider auctionPageFieldsProvider;
    private final FieldRule auctionFieldRule;
    private final FieldRule auctionsPageFieldRule;

    public AuctionServiceImpl(AuctionRepo auctionRepo,
                              TransactionTemplate transactionTemplate,
                              QueryExecutor queryExecutor,
                              Encoder encoder,
                              @Qualifier("auction-field-enricher") FieldEnricher auctionFieldEnricher,
                              @Qualifier("auction-page-field-enricher") PageFieldEnricher auctionPageFieldEnricher,
                              @Qualifier("auction-field-sanitizer") FieldSanitizer auctionFieldSanitizer,
                              @Qualifier("auction-page-field-sanitizer") PageFieldSanitizer auctionPageFieldSanitizer,
                              AuctionValidator auctionValidator,
                              @Qualifier("auction-allowed-fields") AllowedFieldsProvider fullAuctionFieldsProvider,
                              @Qualifier("auction-page-allowed-fields") AllowedFieldsProvider auctionPageFieldsProvider,
                              @Qualifier("auction-field-rule") FieldRule auctionFieldRule,
                              @Qualifier("auction-page-field-rule") FieldRule auctionsPageFieldRule) {
        this.auctionRepo = auctionRepo;
        this.transactionTemplate = transactionTemplate;
        this.queryExecutor = queryExecutor;
        this.encoder = encoder;
        this.auctionFieldEnricher = auctionFieldEnricher;
        this.auctionPageFieldEnricher = auctionPageFieldEnricher;
        this.auctionFieldSanitizer = auctionFieldSanitizer;
        this.auctionPageFieldSanitizer = auctionPageFieldSanitizer;
        this.auctionValidator = auctionValidator;
        this.fullAuctionFieldsProvider = fullAuctionFieldsProvider;
        this.auctionPageFieldsProvider = auctionPageFieldsProvider;
        this.auctionFieldRule = auctionFieldRule;
        this.auctionsPageFieldRule = auctionsPageFieldRule;
    }

    @Override
    public CreatedAuction createAuction(Jwt jwt, CreateAuction auctionInfo) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));

        var codes = encoder.encode(auctionInfo.getIsPrivate());

        String accessCode = codes.accessCode()==null?null:codes.accessCode().code();
        String codeHash = codes.accessCodeHash()==null?null:codes.accessCodeHash().codeHash();
        var auction = AuctionBuilder.buildAuction(userId, auctionInfo, codeHash);

        transactionTemplate.executeWithoutResult(status -> auctionRepo.save(auction));

        return new CreatedAuction(auction.getId(), accessCode);
    }

    @Override
    public DynamicResponse getAuctionInfo(Jwt jwt, UUID auctionId, String password, Set<String> fields) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));

        Map<String, Object> fieldsMap = queryExecutor.selectQueryByField(Auction.class,Map.of("id",auctionId), LogicalOperator.NONE,fields,fullAuctionFieldsProvider,auctionFieldRule);

        Boolean isPrivate = (Boolean) fieldsMap.get("isPrivate");
        String accessCodeHash = (String) fieldsMap.get("accessCodeHash");

        auctionFieldEnricher.enrich(fieldsMap,fields);
        auctionValidator.validate(password,isPrivate,auctionId,userId,accessCodeHash);
        auctionFieldSanitizer.sanitize(fieldsMap,fields);

        return new DynamicResponse(fieldsMap);
    }

    @Override
    public PagedResponse getAllAuctions(Set<String> fields, int size, int page) {
        Pageable pageable = PageRequest.of(page, size);

        var paged = queryExecutor.selectQueryByFieldPaged(
                Auction.class,
                Collections.emptyMap(),
                LogicalOperator.NONE,
                fields,
                auctionPageFieldsProvider,
                auctionsPageFieldRule,
                pageable);

        auctionPageFieldEnricher.enrich(paged,fields);
        auctionPageFieldSanitizer.sanitize(paged,fields);

        return new PagedResponse(
                paged.getContent(),
                paged.getNumber(),
                paged.getSize(),
                paged.getTotalElements(),
                paged.getTotalPages(),
                paged.isFirst(),
                paged.isLast(),
                paged.getNumberOfElements());
    }


    @Override
    public void deleteAuction(UUID id, Jwt jwt) {
        UUID ownerId = auctionRepo.getOwnerIdById(id)
                .orElseThrow(()-> new EntityNotFoundException("Auction not found"));

        UUID userId = UUID.fromString(jwt.getClaim("userId"));

        if (!ownerId.equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }

        int deleted = Optional.ofNullable(transactionTemplate.execute(status-> auctionRepo.deleteAuctionByIdIfAllowed(id)))
                .orElseThrow(()-> new RuntimeException("Auction deletion failed"));

        if (deleted == 0) {
            throw new IllegalStateException("Auction cannot be deleted: there are bids from other users");
        }
    }

    @Override
    public void forcedDeleteAuction(UUID id) {
        int deleted = Optional.ofNullable(transactionTemplate.execute(status->auctionRepo.deleteAuctionById(id)))
                .orElseThrow(()-> new RuntimeException("Auction deletion failed"));

        if (deleted == 0) {
            throw new EntityNotFoundException("Auction not found with id: " + id);
        }
    }

    @Override
    public void changePassword(Jwt jwt, UUID id, ChangePasswordRequest request) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        List<String> roles = jwt.getClaimAsStringList("roles");

        if (roles.contains("ADMIN") || auctionRepo.isOwner(userId,id)) {
            String newPassword = request.newPassword();
            var newCodeHash = encoder.encode(newPassword);
            boolean resetAllowedUsers = request.resetAllowedUsers();

            int updated = Optional.ofNullable(transactionTemplate.execute(status ->
            {
                if (resetAllowedUsers) {
                    auctionRepo.deleteAllowedUsersByAuctionId(id);
                }
                return auctionRepo.setNewCodeHash(newCodeHash.codeHash(),id);
            }))
                    .orElseThrow(()-> new RuntimeException("New code hash could not be updated"));

            if (updated == 0) {
                throw new EntityNotFoundException("Auction not found with id: " + id);
            }
        }else {
            throw new AccessDeniedException("You don't have enough rights to perform this operation");
        }
    }

}
