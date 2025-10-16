package van.karm.auth.infrastructure.selector;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import van.karm.auth.domain.repo.UserRepo;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSelectorImpl implements UserSelector {
    private final UserRepo userRepo;

    @Override
    public Set<UUID> findInactiveUsers() {
        return userRepo.findInactiveUsers();
    }

    @Override
    public Set<UUID> findDisabledUsers() {
        return userRepo.findIdsBannedBefore(LocalDate.now().minusMonths(6));
    }
}