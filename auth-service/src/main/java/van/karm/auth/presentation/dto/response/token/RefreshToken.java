package van.karm.auth.presentation.dto.response.token;

import java.time.LocalDateTime;

public record RefreshToken(String jti, LocalDateTime issuedAt, LocalDateTime expiresAt, String refreshToken) {
}
