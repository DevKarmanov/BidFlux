package van.karm.auth.application.port;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyProvider {
    PrivateKey getPrivateKey();
    PublicKey getPublicKey();
}
