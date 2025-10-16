package van.karm.auth.presentation.controller.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.auth.presentation.dto.request.UserRegData;
import van.karm.auth.presentation.dto.response.token.AccessToken;
import van.karm.auth.presentation.dto.response.token.Tokens;

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
            @RequestBody @Valid UserRegData userRegData
    );

    @PostMapping("/refresh")
    ResponseEntity<AccessToken> refresh(
            @RequestHeader("X-Refresh-Token") @NotBlank(message = "Refresh token must not be empty") String refreshToken
    );

    //todo ендпоинт для восстановления пароля
}



