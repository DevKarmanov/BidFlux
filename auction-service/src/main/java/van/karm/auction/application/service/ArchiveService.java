package van.karm.auction.application.service;

import org.springframework.security.oauth2.jwt.Jwt;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

public interface ArchiveService {
    PagedResponse getMyArchivedAuctions(Jwt jwt, Set<String> fields, int size, int page);
    DynamicResponse getArchivedAuction(Jwt jwt, Set<String> fields, UUID id);
    PagedResponse getAllUserArchivedAuctions(String username, Set<String> fields, int size, int page);
    void forcedDeleteArchivedAuction(UUID id);
}
