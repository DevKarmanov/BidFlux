package van.karm.auth.config.properties;

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