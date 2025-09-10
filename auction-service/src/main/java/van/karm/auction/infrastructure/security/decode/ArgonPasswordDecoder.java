package van.karm.auction.infrastructure.security.decode;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import van.karm.auction.domain.exception.AccessDeniedException;

@RequiredArgsConstructor
@Component
public class ArgonPasswordDecoder implements Decoder {
    private final PasswordEncoder argon2;

    @Override
    public boolean decode(String password, String accessCodeHash) {
        if (password == null || password.isBlank()) {
            throw new AccessDeniedException("Password is required for private auction");
        }

        if (!argon2.matches(password.trim(), accessCodeHash)) {
            throw new AccessDeniedException("Auction password does not match");
        }

        return true;
    }
}
