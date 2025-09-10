package van.karm.auth.infrastructure.service.jwt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import van.karm.auth.application.port.TokenParser;
import van.karm.auth.application.service.jwt.JwtAuthenticationService;
import van.karm.auth.domain.model.RefreshTokenEntity;
import van.karm.auth.domain.repo.RefreshTokenRepo;
import van.karm.auth.infrastructure.service.user.CustomUserDetailsService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private final TokenParser tokenParser;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims;
        try {
            claims = tokenParser.parse(token);
        } catch (JwtException e) {
            return null;
        }

        String jti = claims.getId();
        String username = claims.getSubject();

        // todo переделать этот блок под кеш
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepo.findByJtiAndRevokedFalse(jti).orElse(null);
        if (refreshTokenEntity == null || refreshTokenEntity.isRevoked() || refreshTokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        return null;
    }
}
