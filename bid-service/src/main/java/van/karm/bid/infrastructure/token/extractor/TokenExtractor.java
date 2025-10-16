package van.karm.bid.infrastructure.token.extractor;

public interface TokenExtractor {
    String extractToken(String authHeader);
}
