package van.karm.auction.infrastructure.security.encode;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import van.karm.auction.domain.dto.AccessAndHashCodes;
import van.karm.auction.utils.StringGenerator;

@RequiredArgsConstructor
@Component
public class ArgonPasswordEncoder implements Encoder{
    private final PasswordEncoder argon2;

    @Override
    public AccessAndHashCodes encode(boolean auctionIsPrivate) {
        if (auctionIsPrivate){
            String accessCode = StringGenerator.generateString(64);
            String accessCodeHash = argon2.encode(accessCode);
            return new AccessAndHashCodes(accessCode,accessCodeHash);
        }else {
            return new AccessAndHashCodes(null,null);
        }
    }
}
