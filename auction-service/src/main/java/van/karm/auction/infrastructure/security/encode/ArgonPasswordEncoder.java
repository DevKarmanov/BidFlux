package van.karm.auction.infrastructure.security.encode;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import van.karm.auction.domain.dto.AccessAndHashCodes;
import van.karm.auction.common.utils.StringGenerator;
import van.karm.auction.domain.dto.AccessCode;
import van.karm.auction.domain.dto.AccessCodeHash;

@RequiredArgsConstructor
@Component
public class ArgonPasswordEncoder implements Encoder{
    private final PasswordEncoder argon2;

    @Override
    public AccessAndHashCodes encode(boolean auctionIsPrivate) {
        if (auctionIsPrivate){
            String password = StringGenerator.generateString(64);
            AccessCode accessCode = new AccessCode(password);
            AccessCodeHash accessCodeHash = new AccessCodeHash(argon2.encode(accessCode.code()));
            return new AccessAndHashCodes(accessCode,accessCodeHash);
        }else {
            return new AccessAndHashCodes(null,null);
        }
    }

    @Override
    public AccessCodeHash encode(String password) {
        return new AccessCodeHash(argon2.encode(password));
    }
}
