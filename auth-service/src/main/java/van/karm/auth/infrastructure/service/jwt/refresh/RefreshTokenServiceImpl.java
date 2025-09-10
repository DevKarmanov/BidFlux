package van.karm.auth.infrastructure.service.jwt.refresh;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import van.karm.auth.application.port.JwtSigner;
import van.karm.auth.application.service.jwt.refresh.RefreshTokenService;
import van.karm.auth.domain.repo.RefreshTokenRepo;
import van.karm.auth.infrastructure.config.props.JwtProperties;
import van.karm.auth.presentation.dto.response.token.RefreshToken;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtProperties props;
    private final JwtSigner jwtSigner;

    @Override
    public RefreshToken generate(UUID userId, String username, String deviceId) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.refreshTtlDays(), ChronoUnit.DAYS);
        String jti = UUID.randomUUID().toString();

        refreshTokenRepo.upsertRefreshToken(
                jti,
                userId,
                deviceId,
                LocalDateTime.ofInstant(now, ZoneOffset.UTC),
                LocalDateTime.ofInstant(exp, ZoneOffset.UTC)
        );

        return new RefreshToken(
                jti,
                LocalDateTime.ofInstant(now, ZoneOffset.UTC),
                LocalDateTime.ofInstant(exp, ZoneOffset.UTC),
                generateJwt(username, jti, userId, now)
        );
    }

    @Override
    public String generateJwt(String userName, String jti, UUID userId, Instant now) {
        Instant exp = now.plus(props.refreshTtlDays(), ChronoUnit.DAYS);

        var tokenBuilder = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(props.issuer())
                .setAudience(props.audience())
                .setSubject(userName)
                .setId(jti)
                .claim("userId", userId)
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(exp));

        return jwtSigner.sign(tokenBuilder);
    }
}
