package van.karm.auth.dto.response;

import java.time.LocalDateTime;

public record RefreshToken(String jti, LocalDateTime issuedAt, LocalDateTime expiresAt, String refreshToken) {
}
