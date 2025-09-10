package van.karm.auction.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.domain.factory.AuctionBuilder;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.domain.service.AuctionService;
import van.karm.auction.infrastructure.enricher.AuctionFieldEnricher;
import van.karm.auction.infrastructure.query.QueryExecutor;
import van.karm.auction.infrastructure.sanitizer.AuctionFieldSanitizer;
import van.karm.auction.infrastructure.security.encode.Encoder;
import van.karm.auction.infrastructure.validator.AuctionValidator;
import van.karm.auction.presentation.dto.request.CreateAuction;
import van.karm.auction.presentation.dto.response.CreatedAuction;
import van.karm.auction.presentation.dto.response.DynamicResponse;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepo auctionRepo;
    private final TransactionTemplate transactionTemplate;
    private final QueryExecutor queryExecutor;
    private final Encoder encoder;
    private final AuctionFieldEnricher auctionFieldEnricher;
    private final AuctionFieldSanitizer sanitizer;
    private final AuctionValidator auctionValidator;

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

        Map<String, Object> fieldsMap = queryExecutor.selectQueryById(auctionId, fields);
        Boolean isPrivate = (Boolean) fieldsMap.get("isPrivate");
        String accessCodeHash = (String) fieldsMap.get("accessCodeHash");

        auctionValidator.validate(password,isPrivate,auctionId,userId,accessCodeHash);
        auctionFieldEnricher.enrich(fieldsMap,isPrivate,fields,auctionId);
        sanitizer.sanitize(fieldsMap,fields);

        return new DynamicResponse(fieldsMap);
    }

}
