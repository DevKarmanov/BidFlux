package van.karm.auction.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

    private final AuctionRepo auctionRepo;
    private final PasswordEncoder argon2;

    @Transactional
    @Override
    public CreatedAuction createAuction(CreateAuction auctionInfo) {
        String accessCode = null;
        String accessCodeHash = null;

        if (auctionInfo.getIsPrivate()) {
            accessCode = StringGenerator.generateString(64);
            accessCodeHash = argon2.encode(accessCode);
        }

        Auction auction = new Auction(
                auctionInfo.getTitle(),
                auctionInfo.getDescription(),
                auctionInfo.getStartPrice(),
                auctionInfo.getBidIncrement(),
                auctionInfo.getReservePrice(),
                auctionInfo.getIsPrivate(),
                accessCodeHash,
                AuctionStatus.ACTIVE,     //todo брать время окончания аукциона и заплинировать автоматчиескую установку его статутса на FINISHED
                auctionInfo.getStartDate(),
                auctionInfo.getEndDate(),
                auctionInfo.getCurrency()
        );

        auctionRepo.save(auction);
        return new CreatedAuction(auction.getId(),accessCode);
    }

    @Override
    public AuctionInfo getAuctionInfo(@NotNull UUID id, String password) {
        Auction auction = auctionRepo.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Auction with id "+id+" not found"));

        if (auction.isPrivate()) {
            if (password == null){
                throw new AccessDeniedException("Password is null");
            }
            boolean hasAccess = argon2.matches(password,auction.getAccessCodeHash());
            if (hasAccess) {
                return AuctionMapper.toAuctionInfo(auction,true);
            }else {
                throw new AccessDeniedException("Auction password does not match");
            }
        }else {
            return AuctionMapper.toAuctionInfo(auction,false);
        }

    }
}
