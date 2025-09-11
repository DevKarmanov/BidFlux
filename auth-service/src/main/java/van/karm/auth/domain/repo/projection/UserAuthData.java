package van.karm.auth.domain.repo.projection;

import java.util.Set;

public interface UserAuthData{
    String getUsername();
    Set<String> getRoles();
}
