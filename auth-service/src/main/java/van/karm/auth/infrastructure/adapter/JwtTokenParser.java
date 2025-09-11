package van.karm.auth.infrastructure.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import van.karm.auth.application.port.KeyProvider;
import van.karm.auth.application.port.TokenParser;
import van.karm.auth.infrastructure.config.props.JwtProperties;

@Component
public class JwtTokenParser implements TokenParser {
    private final JwtProperties props;
    private final KeyProvider keyProvider;

    public JwtTokenParser(JwtProperties props, KeyProvider keyProvider) {
        this.props = props;
        this.keyProvider = keyProvider;
    }

    @Override
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(props.issuer())
                .requireAudience(props.audience())
                .setAllowedClockSkewSeconds(30)
                .setSigningKey(keyProvider.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
