package van.karm.auth.application.service.user;

import van.karm.auth.presentation.dto.response.DynamicResponse;

import java.util.Set;

public interface UserService {
    DynamicResponse getUser(String username, Set<String> fields);
}
