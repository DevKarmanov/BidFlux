package van.karm.auth.infrastructure.service.jwt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import van.karm.auth.application.port.TokenParser;
import van.karm.auth.application.service.jwt.JwtAuthenticationService;

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

        String username = claims.getSubject();

        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        return null;
    }
}
