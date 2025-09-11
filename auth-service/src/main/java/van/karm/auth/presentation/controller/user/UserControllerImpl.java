package van.karm.auth.presentation.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import van.karm.auth.application.service.user.UserService;
import van.karm.auth.presentation.dto.response.DynamicResponse;

import java.util.Set;

@RequiredArgsConstructor
@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;

    @Override
    public DynamicResponse getUserInfo(String username, Set<String> fields) {
        return userService.getUser(username, fields);
    }
}
