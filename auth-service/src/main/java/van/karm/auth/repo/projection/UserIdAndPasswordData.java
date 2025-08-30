package van.karm.auth.repo.projection;

import java.util.UUID;

public interface UserIdAndPasswordData {
    UUID getId();
    String getPassword();
}
