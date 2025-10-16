package van.karm.auth.infrastructure.service.jwt.access;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auth.application.port.JwtSigner;
import van.karm.auth.application.port.TokenParser;
import van.karm.auth.application.service.jwt.access.AccessTokenService;
import van.karm.auth.domain.repo.RefreshTokenRepo;
import van.karm.auth.domain.repo.UserRepo;
import van.karm.auth.infrastructure.config.props.JwtProperties;
import van.karm.auth.presentation.dto.response.token.AccessToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AccessTokenServiceImpl implements AccessTokenService {
    private final JwtProperties props;
    private final JwtSigner jwtSigner;
    private final TokenParser tokenParser;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final TransactionTemplate transactionTemplate;

    @Override
    public AccessToken generate(String subject, UUID userId, Collection<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.accessTtlMin(), ChronoUnit.MINUTES);

        var cleanRoles = roles.stream()
                .map(role -> role.replaceFirst("^ROLE_", ""))
                .toList();

        var tokenBuilder = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(props.issuer())
                .setAudience(props.audience())
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .claim("userId",userId)
                .claim("roles", cleanRoles)
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(exp));

        String token = jwtSigner.sign(tokenBuilder);

        transactionTemplate.executeWithoutResult(status -> userRepo.updateUserLastOnlineState(userId));

        long expiresInSeconds = ChronoUnit.SECONDS.between(now, exp);
        return new AccessToken(token, "Bearer", expiresInSeconds);
    }

    @Override
    public AccessToken generateFromRefresh(String refreshToken) {
        var claims = tokenParser.parse(refreshToken);
        var jti = claims.getId();
        var userId = UUID.fromString(claims.get("userId", String.class));

        var projection = refreshTokenRepo.findUserAuthByValidJti(jti)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found or invalid"));

        transactionTemplate.executeWithoutResult(status -> userRepo.updateUserLastOnlineState(userId));

        return generate(projection.getUsername(), userId, projection.getRoles());
    }
}
