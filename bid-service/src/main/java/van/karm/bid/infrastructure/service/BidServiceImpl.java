package van.karm.bid.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.bid.application.service.BidService;
import van.karm.bid.application.validator.AuctionValidator;
import van.karm.bid.domain.exception.AccessDeniedException;
import van.karm.bid.domain.factory.BidFactory;
import van.karm.bid.domain.model.Bid;
import van.karm.bid.domain.repo.BidRepo;
import van.karm.bid.presentation.dto.request.AddBid;

@RequiredArgsConstructor
@Service
public class BidServiceImpl implements BidService {

    private final AuctionValidator auctionValidator;
    private final BidRepo bidRepository;
    private final TransactionTemplate transactionTemplate;

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
}

