package van.karm.bid.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.bid.dto.request.AddBid;
import van.karm.bid.exception.AccessDeniedException;
import van.karm.bid.model.Bid;
import van.karm.bid.repo.BidRepo;
import van.karm.bid.util.factory.BidFactory;
import van.karm.bid.util.validator.AuctionValidator;

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

