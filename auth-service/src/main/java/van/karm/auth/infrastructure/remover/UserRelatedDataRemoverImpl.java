package van.karm.auth.infrastructure.remover;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auth.domain.repo.RefreshTokenRepo;
import van.karm.auth.domain.repo.UserRepo;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRelatedDataRemoverImpl implements UserRelatedDataRemover {
    private final static Logger log = LoggerFactory.getLogger(UserRelatedDataRemoverImpl.class);
    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void cleanTokensAndAllowedUsers(Set<UUID> userIds) {
        if (userIds.isEmpty()) return;
        transactionTemplate.executeWithoutResult(status -> {
            refreshTokenRepo.deleteAllByUserIdIn(userIds);
            userRepo.deleteAllowedUsers(userIds);
            log.info("Deleted refresh tokens and allowed users for {} users", userIds.size());
        });
    }

    @Override
    public void cleanTokensAllowedUsersAndAuctions(Set<UUID> userIds) {
        cleanTokensAndAllowedUsers(userIds);
        transactionTemplate.executeWithoutResult(status -> {
            userRepo.deleteUserAuctions(userIds);
            userRepo.deleteUserArchivedAuctions(userIds);
        });
    }
}
