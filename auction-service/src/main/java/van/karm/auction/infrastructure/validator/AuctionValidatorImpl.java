package van.karm.auction.infrastructure.validator;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.application.validator.AuctionValidator;
import van.karm.auction.presentation.exception.AccessDeniedException;
import van.karm.auction.domain.repo.AuctionRepo;
import van.karm.auction.infrastructure.security.decode.Decoder;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AuctionValidatorImpl implements AuctionValidator {
    private final static Logger log = LoggerFactory.getLogger(AuctionValidatorImpl.class);
    private final TransactionTemplate transactionTemplate;
    private final Decoder decoder;
    private final AuctionRepo auctionRepo;

    @Override
    public void validate(String password, Boolean isPrivate, UUID auctionId, UUID userId, String accessCodeHash) {

        if (Boolean.TRUE.equals(isPrivate)){
            if (password==null){
                throw new AccessDeniedException("A password must be provided for a private auction");
            }else if (!decoder.decode(password, accessCodeHash)){
                throw new AccessDeniedException("Invalid password");
            }
            transactionTemplate.executeWithoutResult(statusTx->auctionRepo.addAllowedUser(auctionId, userId));
            log.info("User {} added to allowed users list", userId);
        }
    }
}
