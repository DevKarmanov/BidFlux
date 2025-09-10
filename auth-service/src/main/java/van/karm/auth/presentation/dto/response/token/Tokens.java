package van.karm.auth.presentation.dto.response.token;

public record Tokens(AccessToken accessToken, RefreshToken refreshToken) {
}
