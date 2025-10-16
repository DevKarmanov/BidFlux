package van.karm.bid.infrastructure.token.extractor;

import org.springframework.stereotype.Component;

@Component
public class TokenExtractorImpl implements TokenExtractor {
    @Override
    public String extractToken(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) return null;
        authHeader = authHeader.trim();
        if (authHeader.toLowerCase().startsWith("bearer ")) {
            return authHeader.substring(7).trim();
        }
        return authHeader;
    }
}
