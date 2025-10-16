package van.karm.auth.presentation.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import van.karm.auth.presentation.dto.request.BlockReasonRequest;
import van.karm.auth.presentation.dto.request.UserUpdateData;
import van.karm.auth.presentation.dto.request.UserUpdatePasswordData;
import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.auth.presentation.dto.response.PagedResponse;

import java.util.Set;

@Validated
@RequestMapping("/user")
public interface UserController {

    @PutMapping("/password/{username}")
    ResponseEntity<Void> updatePassword(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdatePasswordData passwordData);

    @PatchMapping("/{username}")
    ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("username") String username,
            @Valid @RequestBody UserUpdateData userUpdateData);

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

    @DeleteMapping
    ResponseEntity<Void> deleteUser(
            @RequestHeader("Authorization") String bearer
    );

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/switch/block/{username}")
    ResponseEntity<String> switchUserStatus(
            @PathVariable String username,
            @RequestBody(required = false) BlockReasonRequest blockReason
    );

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/forced/{username}")
    ResponseEntity<Void> deleteFullUser(@PathVariable String username);

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/roles/add/{username}")
    ResponseEntity<Void> addRoleToUser(
            @PathVariable String username,
            @RequestParam @NotNull Set<String> roles
    );

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/forced/roles")
    ResponseEntity<Void> forcedDeleteRoles(
            @RequestParam Set<String> roles
    );

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/roles/revoke/{username}")
    ResponseEntity<Void> revokeRoleToUser(
            @PathVariable String username,
            @RequestParam Set<String> roles
    );
}
