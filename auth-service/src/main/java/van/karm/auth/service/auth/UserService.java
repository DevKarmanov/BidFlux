package van.karm.auth.service.auth;

import van.karm.auth.dto.response.Tokens;

public interface UserService {
    Tokens login(String username, String password, String deviceId);
    void register(String username, String password);
}
