package van.karm.auth.infrastructure.service.jwt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import van.karm.auth.application.port.TokenParser;
import van.karm.auth.application.service.jwt.JwtAuthenticationService;
import van.karm.auth.domain.model.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private final TokenParser tokenParser;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims;
        try {
            claims = tokenParser.parse(token);
        } catch (JwtException e) {
            return null;
        }

        if (!claims.containsKey("userId")) {
            throw new IllegalArgumentException("Invalid JWT");
        }

        String username = claims.getSubject();

        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!userDetails.isEnabled() && userDetails instanceof CustomUserDetails customUser) {
                throw new DisabledException("User blocked: " + customUser.getBlockReason());
            }

            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        return null;
    }
}
