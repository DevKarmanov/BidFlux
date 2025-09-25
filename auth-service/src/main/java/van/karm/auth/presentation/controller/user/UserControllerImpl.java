package van.karm.auth.presentation.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auth.application.service.user.UserService;
import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.auth.presentation.dto.response.PagedResponse;

import java.util.Set;

@RequiredArgsConstructor
@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;

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
}
