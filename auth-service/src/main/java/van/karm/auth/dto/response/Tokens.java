package van.karm.auth.dto.response;

public record Tokens(AccessToken access_token, RefreshToken refresh_token) {
}
