package van.karm.auth.application.port;

import io.jsonwebtoken.Claims;

public interface TokenParser {
    Claims parse(String token);
}
