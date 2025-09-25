package van.karm.bid.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.bid.application.checker.UserAllowedChecker;
import van.karm.bid.application.service.BidService;
import van.karm.bid.application.validator.AuctionValidator;
import van.karm.bid.domain.exception.AccessDeniedException;
import van.karm.bid.domain.factory.BidFactory;
import van.karm.bid.domain.model.Bid;
import van.karm.bid.domain.repo.BidRepo;
import van.karm.bid.infrastructure.enricher.BidFieldEnricher;
import van.karm.bid.infrastructure.sanitizer.BidFieldSanitizer;
import van.karm.bid.presentation.dto.request.AddBid;
import van.karm.bid.presentation.dto.response.PagedResponse;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.QueryExecutor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BidServiceImpl implements BidService {

    private final AuctionValidator auctionValidator;
    private final BidRepo bidRepository;
    private final TransactionTemplate transactionTemplate;
    private final UserAllowedChecker userAllowedChecker;
    private final QueryExecutor queryExecutor;
    private final AllowedFieldsProvider allowedFieldsProvider;
    private final FieldRule fieldRule;
    private final BidFieldEnricher bidFieldEnricher;
    private final BidFieldSanitizer bidFieldSanitizer;

    @Override
    public void addBid(Jwt jwt, AddBid bid) {
        String userId = jwt.getClaim("userId");

        if (!auctionValidator.isValid(userId, bid)) {
            throw new AccessDeniedException("Invalid bid request");
        }

        Bid newBid = BidFactory.create(userId, bid);

        transactionTemplate.executeWithoutResult(tx ->
                bidRepository.save(newBid)
        );
    }

    @Override
    public PagedResponse getAllBidsByAuctionId(Jwt jwt, UUID auctionId, int size, int page, Set<String> fields) {
        String userId = jwt.getClaim("userId");

        if (!userAllowedChecker.isUserAllowed(userId,auctionId)){
            throw new AccessDeniedException("You are not allowed to access this resource");
        }

        Map<String, Object> filters = Map.of("auctionId", auctionId);
        Pageable pageable = PageRequest.of(page, size);

        var paged = queryExecutor.selectQueryByFieldPaged(Bid.class,filters,fields,allowedFieldsProvider,fieldRule,pageable);

        bidFieldEnricher.enrich(paged,fields);
        bidFieldSanitizer.sanitize(paged, fields);

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

