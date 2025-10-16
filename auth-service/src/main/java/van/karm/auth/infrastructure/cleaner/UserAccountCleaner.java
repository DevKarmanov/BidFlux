package van.karm.auth.infrastructure.cleaner;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import van.karm.auth.infrastructure.remover.UserRelatedDataRemover;
import van.karm.auth.infrastructure.remover.UserRemover;
import van.karm.auth.infrastructure.selector.UserSelector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAccountCleaner implements Cleaner{
    private final static Logger log = LoggerFactory.getLogger(UserAccountCleaner.class);
    private final UserRemover userRemover;
    private final UserSelector userSelector;
    private final UserRelatedDataRemover userRelatedDataRemover;

    @Override
    public void clean() {
        Set<UUID> inactiveUsers = userSelector.findInactiveUsers();
        Set<UUID> disabledUsers = userSelector.findDisabledUsers();

        boolean inactiveUsersIsEmpty = inactiveUsers.isEmpty();
        boolean disabledUsersIsEmpty = disabledUsers.isEmpty();

        if (inactiveUsersIsEmpty || disabledUsersIsEmpty) {
            log.info("UserAccountCleaner: inactive users and disabled users are empty");
            return;
        }

        Set<UUID> allUsers = new HashSet<>(inactiveUsers.size() + disabledUsers.size());
        allUsers.addAll(inactiveUsers);
        allUsers.addAll(disabledUsers);

        log.info("UserAccountCleaner: found {} inactive users to delete", inactiveUsers.size());
        log.info("UserAccountCleaner: found {} disabled users to mark as deleted", disabledUsers.size());

        userRemover.deleteUsers(inactiveUsers);
        userRemover.markUsersDeleted(disabledUsers);
        userRelatedDataRemover.cleanTokensAndAllowedUsers(allUsers);
    }
}
//todo разослать пользователям за месяц до удаления смс