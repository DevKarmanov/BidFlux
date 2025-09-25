package van.karm.auth.infrastructure.service.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import van.karm.auth.application.service.user.UserService;
import van.karm.auth.domain.model.UserEntity;
import van.karm.auth.infrastructure.enricher.UserFieldEnricher;
import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.auth.presentation.dto.response.PagedResponse;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.QueryExecutor;

import java.util.Collections;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final QueryExecutor queryExecutor;
    private final UserFieldEnricher userFieldEnricher;
    private final AllowedFieldsProvider userAllowedFieldsProvider;
    private final AllowedFieldsProvider userPageAllowedFieldsProvider;

    public UserServiceImpl(QueryExecutor queryExecutor, UserFieldEnricher userFieldEnricher, @Qualifier("user") AllowedFieldsProvider userAllowedFieldsProvider, @Qualifier("users") AllowedFieldsProvider userPageAllowedFieldsProvider) {
        this.queryExecutor = queryExecutor;
        this.userFieldEnricher = userFieldEnricher;
        this.userAllowedFieldsProvider = userAllowedFieldsProvider;
        this.userPageAllowedFieldsProvider = userPageAllowedFieldsProvider;
    }

    @Override
    public DynamicResponse getUser(String username, Set<String> fields) {
        FieldRule noOpRule = (s1,s2) -> {};

        var response = queryExecutor.selectQueryByField(UserEntity.class,"username",username,fields,userAllowedFieldsProvider,noOpRule);
        userFieldEnricher.enrich(response,fields,username);

        return new DynamicResponse(response);
    }

    @Override
    public PagedResponse getAllUsers(int size, int page, Set<String> fields) {
        FieldRule noOpRule = (s1,s2) -> {};
        Pageable pageable = PageRequest.of(page, size);

        var paged = queryExecutor.selectQueryByFieldPaged(UserEntity.class, Collections.emptyMap(), fields,userPageAllowedFieldsProvider,noOpRule,pageable);

        return new PagedResponse(
                paged.getContent(),
                paged.getNumber(),
                paged.getSize(),
                paged.getTotalElements(),
                paged.getTotalPages(),
                paged.isFirst(),
                paged.isLast(),
                paged.getNumberOfElements()
        );
    }

    //todo при удалении аккаунта пользователя оставлять ник и id (нужно чтобы можно было смотреть историю ставок и прочее)
}
