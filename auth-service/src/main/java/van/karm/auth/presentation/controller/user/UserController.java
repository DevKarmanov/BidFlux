package van.karm.auth.presentation.controller.user;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import van.karm.auth.presentation.dto.response.DynamicResponse;

import java.util.Set;

@Validated
@RequestMapping("/user")
public interface UserController {

    @GetMapping("/{username}")
    DynamicResponse getUserInfo(
            @PathVariable @NotBlank(message = "The name must be specified") String username,
            @RequestParam(required = false) Set<String> fields);
}
