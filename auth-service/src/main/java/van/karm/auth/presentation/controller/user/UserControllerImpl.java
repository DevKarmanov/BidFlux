package van.karm.auth.presentation.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auth.application.service.user.UserService;
import van.karm.auth.presentation.dto.request.BlockReasonRequest;
import van.karm.auth.presentation.dto.request.UserUpdateData;
import van.karm.auth.presentation.dto.request.UserUpdatePasswordData;
import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.auth.presentation.dto.response.PagedResponse;

import java.util.Set;

@RequiredArgsConstructor
@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;

    @Override
    public ResponseEntity<Void> updatePassword(String username, UserDetails userDetails, UserUpdatePasswordData passwordData) {
        userService.updatePassword(username, userDetails, passwordData);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<Void> updateUser(UserDetails userDetails, String username, UserUpdateData userUpdateData) {
        userService.updateUser(userDetails, username, userUpdateData);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<DynamicResponse> getUserInfo(String username, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUser(username, fields));
    }

    @Override
    public ResponseEntity<PagedResponse> getAllUsers(int size, int page, Set<String> fields) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getAllUsers(size, page, fields));
    }

    @Override
    public ResponseEntity<Void> deleteUser(String bearer) {
        userService.deleteUser(bearer.substring(7));
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<String> switchUserStatus(String username, BlockReasonRequest blockReason) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.switchUserStatus(username,blockReason));
    }

    @Override
    public ResponseEntity<Void> deleteFullUser(String username) {
        userService.deleteFullUser(username);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<Void> addRoleToUser(String username, Set<String> roles) {
        userService.addRoleToUser(username,roles);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<Void> forcedDeleteRoles(Set<String> roles) {
        userService.forcedDeleteRoles(roles);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    public ResponseEntity<Void> revokeRoleToUser(String username, Set<String> roles) {
        userService.revokeRoleToUser(username,roles);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
