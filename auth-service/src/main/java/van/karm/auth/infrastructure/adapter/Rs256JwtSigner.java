package van.karm.auth.infrastructure.adapter;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import van.karm.auth.application.port.JwtSigner;
import van.karm.auth.application.port.KeyProvider;

@RequiredArgsConstructor
@Component
public class Rs256JwtSigner implements JwtSigner {
    private final KeyProvider keyProvider;

    @Override
    public String sign(JwtBuilder builder) {
        return builder.signWith(keyProvider.getPrivateKey(), SignatureAlgorithm.RS256).compact();
    }
}
