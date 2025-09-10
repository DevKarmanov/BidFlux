package van.karm.auth.infrastructure.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.jwt")
public record JwtProperties(
        String issuer,
        String audience,
        String privateKeyPath,
        String publicKeyPath,
        int accessTtlMin,
        int refreshTtlDays
) {}