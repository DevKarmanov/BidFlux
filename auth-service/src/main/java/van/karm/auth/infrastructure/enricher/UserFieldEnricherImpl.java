package van.karm.auth.infrastructure.enricher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import van.karm.auth.application.enricher.FieldEnricher;
import van.karm.auth.domain.repo.UserRepo;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class UserFieldEnricherImpl implements FieldEnricher {
    private final UserRepo userRepo;

    @Override
    public void enrich(Map<String, Object> fieldsMap, Set<String> requestedFields, String username) {
        if (requestedFields!=null && !requestedFields.isEmpty()) {
            if (requestedFields.contains("roles")) {
                fieldsMap.put("roles",userRepo.findRoleNamesByUsername(username));
            }
        }
    }
}
