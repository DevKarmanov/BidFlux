package van.karm.auction.infrastructure.security.decode;

public interface Decoder {
    boolean decode(String password, String accessCodeHash);
}
