package van.karm.auth.application.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import van.karm.auth.presentation.dto.request.BlockReasonRequest;
import van.karm.auth.presentation.dto.request.UserUpdateData;
import van.karm.auth.presentation.dto.request.UserUpdatePasswordData;
import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.auth.presentation.dto.response.PagedResponse;

import java.util.Set;

public interface UserService {
    DynamicResponse getUser(String username, Set<String> fields);
    PagedResponse getAllUsers(int size, int page, Set<String> fields);
    void deleteUser(String token);
    String switchUserStatus(String username, BlockReasonRequest blockReason);
    void deleteFullUser(String username);
    void addRoleToUser(String username, Set<String> roles);
    void revokeRoleToUser(String username, Set<String> roles);
    void updatePassword(String username, UserDetails userDetails, UserUpdatePasswordData passwordData);
    void updateUser(UserDetails userDetails, String username, UserUpdateData userUpdateData);
    void forcedDeleteRoles(Set<String> roles);
}
