package van.karm.auth.infrastructure.service.jwt.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import van.karm.auth.application.service.jwt.JwtAuthenticationService;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthenticationService jwtAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = resolveToken(request);
            if (token != null) {
                Authentication auth = jwtAuthenticationService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        } catch (DisabledException e) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "User blocked", e.getMessage(), request.getRequestURI());
        } catch (JwtException | AuthenticationException e) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", e.getMessage(), request.getRequestURI());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void writeError(HttpServletResponse response, int status, String error, String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
            {
              "status":%d,
              "error":"%s",
              "message":"%s",
              "path":"%s",
              "timestamp":"%s"
            }
            """.formatted(status, error, message, path, Instant.now()));
    }
}