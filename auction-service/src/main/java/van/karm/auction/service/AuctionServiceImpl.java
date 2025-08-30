package van.karm.auction.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auction.dto.request.CreateAuction;
import van.karm.auction.dto.response.AuctionInfo;
import van.karm.auction.dto.response.CreatedAuction;
import van.karm.auction.exception.AccessDeniedException;
import van.karm.auction.model.Auction;
import van.karm.auction.model.AuctionStatus;
import van.karm.auction.repo.AuctionRepo;
import van.karm.auction.utils.StringGenerator;
import van.karm.auction.utils.mapper.AuctionMapper;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService{
    private final Logger log = LoggerFactory.getLogger(AuctionServiceImpl.class);
    private final AuctionRepo auctionRepo;
    private final PasswordEncoder argon2;
    private final TransactionTemplate transactionTemplate;


    @Override
    public CreatedAuction createAuction(CreateAuction auctionInfo) {
        String accessCode;
        String accessCodeHash = null;

        if (auctionInfo.getIsPrivate()) {
            accessCode = StringGenerator.generateString(64);
            accessCodeHash = argon2.encode(accessCode);
        } else {
            accessCode = null;
        }

        Auction auction = new Auction(
                auctionInfo.getTitle(),
                auctionInfo.getDescription(),
                auctionInfo.getStartPrice(),
                auctionInfo.getBidIncrement(),
                auctionInfo.getReservePrice(),
                auctionInfo.getIsPrivate(),//todo установить хозяина аукциона
                accessCodeHash,
                AuctionStatus.ACTIVE,     //todo брать время окончания аукциона и запланировать автоматическую установку его статуса на FINISHED
                auctionInfo.getStartDate(),
                auctionInfo.getEndDate(),
                auctionInfo.getCurrency()
        );

        return transactionTemplate.execute(status -> {
            auctionRepo.save(auction);
            return new CreatedAuction(auction.getId(), accessCode);
        });
    }


    @Override
    public AuctionInfo getAuctionInfo(Jwt jwt, UUID auctionId, String password) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));

        Auction auction = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("Auction with id " + auctionId + " not found"));

        boolean isAllowed = !auction.isPrivate() || checkAndGrantAccess(auctionId, userId, password, auction.getAccessCodeHash());

        return AuctionMapper.toAuctionInfo(auction, isAllowed);
    }

    private boolean checkAndGrantAccess(UUID auctionId, UUID userId, String password, String accessCodeHash) {
        if (password == null || password.isBlank()) {
            throw new AccessDeniedException("Password is required for private auction");
        }

        if (!argon2.matches(password.trim(), accessCodeHash)) {
            throw new AccessDeniedException("Auction password does not match");
        }

        transactionTemplate.executeWithoutResult(statusTx->auctionRepo.addAllowedUser(auctionId, userId));
        log.info("User {} added to allowed users list", userId);

        return true;
    }


}
