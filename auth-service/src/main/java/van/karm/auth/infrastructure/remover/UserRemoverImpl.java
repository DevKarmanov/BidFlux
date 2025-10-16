package van.karm.auth.infrastructure.remover;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auth.domain.repo.UserRepo;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRemoverImpl implements UserRemover {
    private final static Logger log = LoggerFactory.getLogger(UserRemoverImpl.class);
    private final UserRepo userRepo;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void deleteUsers(Set<UUID> userIds) {
        if (userIds.isEmpty()) return;
        transactionTemplate.executeWithoutResult(status -> {
            userRepo.deleteAllById(userIds);
            log.info("Deleted {} inactive users", userIds.size());
        });
    }

    @Override
    public void markUsersDeleted(Set<UUID> userIds) {
        if (userIds.isEmpty()) return;
        transactionTemplate.executeWithoutResult(status -> {
            userRepo.markUsersDeleted(userIds);
            log.info("Marked {} disabled users as deleted", userIds.size());
        });
    }
}
