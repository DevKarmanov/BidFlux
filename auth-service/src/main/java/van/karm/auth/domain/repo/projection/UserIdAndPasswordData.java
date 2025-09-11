package van.karm.auth.domain.repo.projection;

import java.util.UUID;

public interface UserIdAndPasswordData {
    UUID getId();
    String getPassword();
}
