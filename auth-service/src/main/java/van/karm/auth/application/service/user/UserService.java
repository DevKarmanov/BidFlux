package van.karm.auth.application.service.user;

import van.karm.auth.presentation.dto.response.token.Tokens;

public interface UserService {
    Tokens login(String username, String password, String deviceId);
    void register(String username, String password);
}
