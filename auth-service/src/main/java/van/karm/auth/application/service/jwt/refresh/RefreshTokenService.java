package van.karm.auth.application.service.jwt.refresh;

import van.karm.auth.presentation.dto.response.token.RefreshToken;

import java.time.Instant;
import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken generate(UUID userId, String username, String deviceId);
    String generateJwt(String userName, String jti, UUID userId, Instant now);
}
