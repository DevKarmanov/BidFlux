package van.karm.auth.infrastructure.remover;

import java.util.Set;
import java.util.UUID;

public interface UserRemover {
    void deleteUsers(Set<UUID> userIds);
    void markUsersDeleted(Set<UUID> userIds);
}
