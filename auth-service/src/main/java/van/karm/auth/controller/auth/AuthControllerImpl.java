package van.karm.auth.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auth.dto.response.AccessToken;
import van.karm.auth.dto.response.Tokens;
import van.karm.auth.service.auth.UserService;
import van.karm.auth.service.jwt.JwtService;

@RequiredArgsConstructor
@RestController
public class AuthControllerImpl implements AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<Tokens> login(String username, String password, String deviceId) {
        return ResponseEntity.ok(userService.login(username, password, deviceId));
    }

    @Override
    public ResponseEntity<Void> register(String username, String password) {
        userService.register(username, password);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<AccessToken> refresh(String refreshToken) {
        return ResponseEntity.ok(jwtService.generateAccessTokenFromRefresh(refreshToken));
    }
}