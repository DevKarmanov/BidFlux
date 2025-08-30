package van.karm.auth.dto.response;

public record Tokens(AccessToken accessToken, RefreshToken refreshToken) {
}
