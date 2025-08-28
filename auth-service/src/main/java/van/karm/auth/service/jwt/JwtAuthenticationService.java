package van.karm.auth.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import van.karm.auth.model.RefreshTokenEntity;
import van.karm.auth.repo.RefreshTokenRepo;
import van.karm.auth.service.auth.CustomUserDetailsService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenRepo refreshTokenRepo;

    public Authentication getAuthentication(String token) {
        Claims claims;
        try {
            claims = jwtService.parse(token);
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
