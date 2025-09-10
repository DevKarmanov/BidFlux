package van.karm.auth.infrastructure.config;

import io.grpc.Metadata;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import van.karm.auth.presentation.exception.handler.RestAccessDeniedHandler;
import van.karm.auth.presentation.exception.handler.RestAuthenticationEntryPoint;
import van.karm.auth.infrastructure.config.props.JwtProperties;
import van.karm.auth.infrastructure.service.jwt.filter.JwtAuthFilter;
import van.karm.auth.infrastructure.service.jwt.auth.JwtAuthenticationServiceImpl;

@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
@Configuration
public class AuthConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationServiceImpl jwtAuthenticationService;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/swagger-resources/configuration/ui",
            "/swagger-resources/configuration/security",
            "/openapi.yaml"
    };
    private static final String[] ACTUATOR_WHITELIST = {
            "/actuator",
            "/actuator/**"
    };

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return (call, headers) -> {
            String token = headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                return jwtAuthenticationService.getAuthentication(token);
            }
            return null;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ВАЖНО!
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/.well-known/jwks.json").permitAll()
                        .requestMatchers(ACTUATOR_WHITELIST).permitAll()
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
