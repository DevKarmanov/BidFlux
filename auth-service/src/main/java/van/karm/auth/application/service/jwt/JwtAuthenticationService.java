package van.karm.auth.application.service.jwt;

import org.springframework.security.core.Authentication;

public interface JwtAuthenticationService {
    Authentication getAuthentication(String token);
}
