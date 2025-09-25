package van.karm.auction.infrastructure.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.application.service.AuctionService;
import van.karm.auction.domain.factory.AuctionBuilder;
import van.karm.auction.domain.model.Auction;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.infrastructure.enricher.AuctionFieldEnricher;
import van.karm.auction.infrastructure.sanitizer.AuctionFieldSanitizer;
import van.karm.auction.infrastructure.security.encode.Encoder;
import van.karm.auction.infrastructure.validator.AuctionValidator;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.QueryExecutor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepo auctionRepo;
    private final TransactionTemplate transactionTemplate;
    private final QueryExecutor queryExecutor;
    private final Encoder encoder;
    private final AuctionFieldEnricher auctionFieldEnricher;
    private final AuctionFieldSanitizer sanitizer;
    private final AuctionValidator auctionValidator;
    private final AllowedFieldsProvider fullFieldsProvider;
    private final FieldRule auctionFieldRule;
    private final FieldRule auctionsPageFieldRule;

    public AuctionServiceImpl(AuctionRepo auctionRepo,
                              TransactionTemplate transactionTemplate,
                              QueryExecutor queryExecutor,
                              Encoder encoder,
                              AuctionFieldEnricher auctionFieldEnricher,
                              AuctionFieldSanitizer sanitizer,
                              AuctionValidator auctionValidator,
                              AllowedFieldsProvider fullFieldsProvider,
                              @Qualifier("auction") FieldRule auctionFieldRule,
                              @Qualifier("auctions") FieldRule auctionsPageFieldRule) {
        this.auctionRepo = auctionRepo;
        this.transactionTemplate = transactionTemplate;
        this.queryExecutor = queryExecutor;
        this.encoder = encoder;
        this.auctionFieldEnricher = auctionFieldEnricher;
        this.sanitizer = sanitizer;
        this.auctionValidator = auctionValidator;
        this.fullFieldsProvider = fullFieldsProvider;
        this.auctionFieldRule = auctionFieldRule;
        this.auctionsPageFieldRule = auctionsPageFieldRule;
    }


    @Override
    public CreatedAuction createAuction(Jwt jwt, CreateAuction auctionInfo) { //todo аукционы сами не удаляются и не удаляются когда аккаунт владельца стирается, нужно придумать механизм стирания всех бесполезных аукционов (созданых по приколу) и не трогать какие-то важные завершившиеся
        if (!jwt.hasClaim("userId")){
            throw new IllegalArgumentException("Invalid JWT");
        }

        UUID userId = UUID.fromString(jwt.getClaim("userId"));

        var codes = encoder.encode(auctionInfo.getIsPrivate());
        var auction = AuctionBuilder.buildAuction(userId, auctionInfo, codes.accessCodeHash());

        transactionTemplate.executeWithoutResult(status -> auctionRepo.save(auction));

        return new CreatedAuction(auction.getId(), codes.accessCode());
    }

    @Override
    public DynamicResponse getAuctionInfo(Jwt jwt, UUID auctionId, String password, Set<String> fields) {
        if (!jwt.hasClaim("userId")){
            throw new IllegalArgumentException("Invalid JWT");
        }

        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        Map<String, Object> fieldsMap = queryExecutor.selectQueryByField(Auction.class,"id",auctionId,fields,fullFieldsProvider,auctionFieldRule);

        Boolean isPrivate = (Boolean) fieldsMap.get("isPrivate");
        String accessCodeHash = (String) fieldsMap.get("accessCodeHash");

        auctionFieldEnricher.enrich(fieldsMap,isPrivate,fields,auctionId);
        auctionValidator.validate(password,isPrivate,auctionId,userId,accessCodeHash);
        sanitizer.sanitize(fieldsMap,fields);

        return new DynamicResponse(fieldsMap);
    }

    @Override
    public PagedResponse getAllAuctions(Set<String> fields, int size, int page) {
        Pageable pageable = PageRequest.of(page, size);

        var paged = queryExecutor.selectQueryByFieldPaged(Auction.class,Collections.emptyMap(),fields,fullFieldsProvider,auctionsPageFieldRule,pageable);

        auctionFieldEnricher.enrich(paged,fields);
        sanitizer.sanitize(paged,fields);

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

}
