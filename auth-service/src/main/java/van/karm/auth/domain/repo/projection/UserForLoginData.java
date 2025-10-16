package van.karm.auth.domain.repo.projection;

import java.util.UUID;

public interface UserForLoginData {
    UUID getId();
    String getPassword();
    Boolean getDeleted();
    Boolean getEnabled();
    String getBlockReason();
}
