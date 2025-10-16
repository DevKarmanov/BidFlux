package van.karm.auth.application.service.auth;

import van.karm.auth.presentation.dto.request.UserRegData;
import van.karm.auth.presentation.dto.response.token.Tokens;

public interface AuthService {
    Tokens login(String username, String password, String deviceId);
    void register(UserRegData userRegData);
}
