package van.karm.auth.infrastructure.selector;

import java.util.Set;
import java.util.UUID;

public interface UserSelector {
    Set<UUID> findInactiveUsers();
    Set<UUID> findDisabledUsers();
}
