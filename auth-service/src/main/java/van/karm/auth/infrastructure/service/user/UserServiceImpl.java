package van.karm.auth.infrastructure.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import van.karm.auth.application.service.user.UserService;
import van.karm.auth.domain.model.UserEntity;
import van.karm.auth.infrastructure.enricher.UserFieldEnricher;
import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.infrastructure.query.QueryExecutor;
import van.karm.shared.application.rule.FieldRule;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final QueryExecutor queryExecutor;
    private final UserFieldEnricher userFieldEnricher;
    private final AllowedFieldsProvider allowedFieldsProvider;

    @Override
    public DynamicResponse getUser(String username, Set<String> fields) {
        FieldRule noOpRule = (s1,s2) -> {};

        var response = queryExecutor.selectQueryByField(UserEntity.class,"username",username,fields,allowedFieldsProvider,noOpRule);
        userFieldEnricher.enrich(response,fields,username);

        return new DynamicResponse(response);
    }
}
