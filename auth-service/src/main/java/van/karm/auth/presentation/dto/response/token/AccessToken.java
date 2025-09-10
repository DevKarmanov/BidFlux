package van.karm.auth.presentation.dto.response.token;

public record AccessToken(String accessToken, String tokenType, long expiresAt) {
}
