package van.karm.auth.application.port;

import io.jsonwebtoken.JwtBuilder;

public interface JwtSigner {
    String sign(JwtBuilder builder);
}
