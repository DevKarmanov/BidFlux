package van.karm.auth.dto.response;

public record AccessToken(String accessToken, String tokenType, long expiresAt) {
}
