package van.karm.auth.application.service.jwt.access;

import van.karm.auth.presentation.dto.response.token.AccessToken;

import java.util.Collection;
import java.util.UUID;

public interface AccessTokenService {
    AccessToken generate(String subject, UUID userId, Collection<String> roles);
    AccessToken generateFromRefresh(String refreshToken);
}
