package van.karm.auth.application.service.user;

import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.auth.presentation.dto.response.PagedResponse;

import java.util.Set;

public interface UserService {
    DynamicResponse getUser(String username, Set<String> fields);
    PagedResponse getAllUsers(int size, int page, Set<String> fields);
}
