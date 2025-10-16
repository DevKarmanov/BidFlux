package van.karm.auth.presentation.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auth.application.service.jwt.access.AccessTokenService;
import van.karm.auth.application.service.auth.AuthService;
import van.karm.auth.presentation.dto.request.UserRegData;
import van.karm.auth.presentation.dto.response.token.AccessToken;
import van.karm.auth.presentation.dto.response.token.Tokens;

@RequiredArgsConstructor
@RestController
public class AuthControllerImpl implements AuthController {
    private final AuthService userService;
    private final AccessTokenService accessTokenService;

    @Override
    public ResponseEntity<Tokens> login(String username, String password, String deviceId) {
        return ResponseEntity.ok(userService.login(username, password, deviceId));
    }

    @Override
    public ResponseEntity<Void> register(UserRegData userRegData) {
        userService.register(userRegData);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<AccessToken> refresh(String refreshToken) {
        return ResponseEntity.ok(accessTokenService.generateFromRefresh(refreshToken));
    }
}