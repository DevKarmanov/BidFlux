package van.karm.auction.presentation.controller.archive;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auction.application.service.ArchiveService;
import van.karm.auction.presentation.dto.response.DynamicResponse;
import van.karm.auction.presentation.dto.response.PagedResponse;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ArchiveControllerImpl implements ArchiveController {
    private final ArchiveService archiveService;

    @Override
    public ResponseEntity<PagedResponse> getMyArchivedAuctions(Jwt jwt, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getMyArchivedAuctions(jwt,fields,size,page));
    }

    @Override
    public ResponseEntity<DynamicResponse> getArchivedAuction(Jwt jwt, UUID id, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getArchivedAuction(jwt,fields,id));
    }

    @Override
    public ResponseEntity<PagedResponse> getUserArchivedAuctions(String username, int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(archiveService.getAllUserArchivedAuctions(username,fields,size,page));
    }

    @Override
    public ResponseEntity<Void> forcedDeleteArchivedAuction(UUID id) {
        archiveService.forcedDeleteArchivedAuction(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
