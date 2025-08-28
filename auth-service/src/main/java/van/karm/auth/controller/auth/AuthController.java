package van.karm.auth.controller.auth;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import van.karm.auth.dto.response.AccessToken;
import van.karm.auth.dto.response.Tokens;

@RequestMapping("/auth")
@Validated
public interface AuthController {

    @PostMapping("/login")
    ResponseEntity<Tokens> login(
            @RequestParam @NotBlank(message = "Username must not be empty") String username,
            @RequestParam @NotBlank(message = "Password must not be empty") String password,
            @RequestHeader("X-Device-Id") @NotBlank(message = "The device ID must not be empty") String deviceId
    );

    @PostMapping("/registration")
    ResponseEntity<Void> register(
            @RequestParam @NotBlank(message = "Username must not be empty") String username,
            @RequestParam @NotBlank(message = "Password must not be empty") String password
    );

    @PostMapping("/refresh")
    ResponseEntity<AccessToken> refresh(
            @RequestHeader("X-Refresh-Token") @NotBlank(message = "Refresh token must not be empty") String refreshToken
    );
}



