package van.karm.auth.dto.response;

public record AccessToken(String access_token, String token_type, long expires_at) {
}
