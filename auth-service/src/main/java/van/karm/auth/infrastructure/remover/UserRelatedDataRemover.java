package van.karm.auth.infrastructure.remover;

import java.util.Set;
import java.util.UUID;

public interface UserRelatedDataRemover {
    void cleanTokensAndAllowedUsers(Set<UUID> userIds);
    void cleanTokensAllowedUsersAndAuctions(Set<UUID> userIds);
}
