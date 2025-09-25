package van.karm.auth.presentation.controller.user;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.auth.presentation.dto.response.PagedResponse;

import java.util.Set;

@Validated
@RequestMapping("/user")
public interface UserController {

    @GetMapping("/{username}")
    ResponseEntity<DynamicResponse> getUserInfo(
            @PathVariable @NotBlank(message = "The name must be specified") String username,
            @RequestParam(required = false) Set<String> fields);

    @GetMapping("/getAll")
    ResponseEntity<PagedResponse> getAllUsers(
            @RequestParam(required = false,defaultValue = "5") int size,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false) Set<String> fields
    );
}
