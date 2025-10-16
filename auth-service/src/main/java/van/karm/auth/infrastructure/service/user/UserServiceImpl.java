package van.karm.auth.infrastructure.service.user;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auth.application.enricher.FieldEnricher;
import van.karm.auth.application.port.TokenParser;
import van.karm.auth.application.service.user.UserService;
import van.karm.auth.domain.model.UserEntity;
import van.karm.auth.domain.repo.RefreshTokenRepo;
import van.karm.auth.domain.repo.UserRepo;
import van.karm.auth.domain.repo.UserRoleRepo;
import van.karm.auth.infrastructure.normalizer.Normalizer;
import van.karm.auth.infrastructure.remover.UserRelatedDataRemover;
import van.karm.auth.infrastructure.remover.UserRemover;
import van.karm.auth.infrastructure.validator.Validator;
import van.karm.auth.presentation.dto.request.BlockReasonRequest;
import van.karm.auth.presentation.dto.request.UserUpdateData;
import van.karm.auth.presentation.dto.request.UserUpdatePasswordData;
import van.karm.auth.presentation.dto.response.DynamicResponse;
import van.karm.auth.presentation.dto.response.PagedResponse;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.QueryExecutor;
import van.karm.shared.infrastructure.query.builder.LogicalOperator;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final Normalizer normalizer;
    private final UserRoleRepo userRoleRepo;
    private final QueryExecutor queryExecutor;
    private final FieldEnricher userFieldEnricher;
    private final AllowedFieldsProvider userAllowedFieldsProvider;
    private final AllowedFieldsProvider userPageAllowedFieldsProvider;
    private final TokenParser tokenParser;
    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRemover userRemover;
    private final UserRelatedDataRemover userRelatedDataRemover;
    private final TransactionTemplate transactionTemplate;
    private final Validator specialNameValidator;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(Normalizer normalizer,
                           UserRoleRepo userRoleRepo,
                           QueryExecutor queryExecutor,
                           FieldEnricher userFieldEnricher,
                           @Qualifier("user") AllowedFieldsProvider userAllowedFieldsProvider,
                           @Qualifier("users") AllowedFieldsProvider userPageAllowedFieldsProvider,
                           TokenParser tokenParser,
                           UserRepo userRepo,
                           RefreshTokenRepo refreshTokenRepo,
                           UserRemover userRemover,
                           UserRelatedDataRemover userRelatedDataRemover,
                           TransactionTemplate transactionTemplate,
                           @Qualifier("special-name-validator") Validator specialNameValidator, PasswordEncoder passwordEncoder) {
        this.normalizer = normalizer;
        this.userRoleRepo = userRoleRepo;
        this.queryExecutor = queryExecutor;
        this.userFieldEnricher = userFieldEnricher;
        this.userAllowedFieldsProvider = userAllowedFieldsProvider;
        this.userPageAllowedFieldsProvider = userPageAllowedFieldsProvider;
        this.tokenParser = tokenParser;
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.transactionTemplate = transactionTemplate;
        this.specialNameValidator = specialNameValidator;
        this.userRemover = userRemover;
        this.userRelatedDataRemover = userRelatedDataRemover;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public DynamicResponse getUser(String username, Set<String> fields) {
        FieldRule noOpRule = (s1,s2) -> {};

        specialNameValidator.validate(username);

        var response = queryExecutor.selectQueryByField(UserEntity.class,Map.of("username",username), LogicalOperator.NONE,fields,userAllowedFieldsProvider,noOpRule);
        userFieldEnricher.enrich(response,fields,username);

        return new DynamicResponse(response);
    }


    @Override
    public PagedResponse getAllUsers(int size, int page, Set<String> fields) {
        FieldRule noOpRule = (s1,s2) -> {};
        Pageable pageable = PageRequest.of(page, size);

        var paged = queryExecutor.selectQueryByFieldPaged(UserEntity.class, Map.of("deleted",false,"enabled",true),LogicalOperator.AND, fields,userPageAllowedFieldsProvider,noOpRule,pageable);

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

    @Override
    public void deleteUser(String token) {
        Claims claims = tokenParser.parse(token);
        UUID userId = UUID.fromString(claims.get("userId").toString());
        //полностью удалить если: чел нигде не побеждал, не создал ни одного аукциона/все аукционы неактивны

        boolean userWasWinner = userRepo.userWasWinner(userId);
        boolean userHasNoActiveOrArchivedAuctions = userRepo.hasNoActiveOrArchivedAuctions(userId);

        if (!userWasWinner && userHasNoActiveOrArchivedAuctions) {
            userRemover.deleteUsers(Set.of(userId));
        }else {
            userRemover.markUsersDeleted(Set.of(userId));
        }
        userRelatedDataRemover.cleanTokensAndAllowedUsers(Set.of(userId));
    }

    @Override
    public String switchUserStatus(String username, BlockReasonRequest blockReasonRequest) {
        specialNameValidator.validate(username);

        boolean currentlyEnabled = userRepo.getUserEnabledByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String reason = blockReasonRequest.reason();
        if (reason == null || reason.trim().isEmpty()){
            throw new IllegalArgumentException("Block reason cannot be empty");
        }

        transactionTemplate.executeWithoutResult(status -> {
            userRepo.updateUserStatus(username, !currentlyEnabled, currentlyEnabled ? reason : null);

            if (currentlyEnabled) {
                refreshTokenRepo.revokeAllTokensByUsername(username);
            }
        });

        return currentlyEnabled ? "User blocked: " + reason : "User unblocked";
    }//todo отправлять смс с причиной блокировки

    @Override
    public void deleteFullUser(String username) {
        specialNameValidator.validate(username);

        UUID userId = userRepo.getUserIdByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRemover.deleteUsers(Set.of(userId));
        userRelatedDataRemover.cleanTokensAllowedUsersAndAuctions(Set.of(userId));
    }

    @Override
    public void addRoleToUser(String username, Set<String> roles) {
        specialNameValidator.validate(username);

        UUID userId = userRepo.getUserIdByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<String> normalizedRoles = normalizer.normalize(roles);

        int updated = Optional.ofNullable(transactionTemplate.execute(status ->
                {
                    userRoleRepo.createMissingRoles(normalizedRoles);
                    return userRoleRepo.addRolesToUser(userId, normalizedRoles);
                }))
                .orElseThrow(() -> new RuntimeException("Failed to add roles to user"));

        if (updated == 0) {
            throw new IllegalArgumentException("Roles have not been added, they may have already been assigned to this user");
        }
    }

    @Override
    public void revokeRoleToUser(String username, Set<String> roles) {
        specialNameValidator.validate(username);

        UUID userId = userRepo.getUserIdByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<String> normalizedRoles = normalizer.normalize(roles);

        int updated = Optional.ofNullable(transactionTemplate.execute(status ->
                        userRoleRepo.removeRolesFromUser(userId, normalizedRoles)))
                .orElseThrow(() -> new RuntimeException("Failed to remove roles to user"));

        if (updated == 0) {
            throw new IllegalArgumentException("The roles have not been revoked, check if the roles or the user exist");
        }
    }

    @Override
    public void updatePassword(String username, UserDetails userDetails, UserUpdatePasswordData passwordData) {
        specialNameValidator.validate(username);

        String myUsername = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!myUsername.equals(username) && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to update password");
        }

        if (!isAdmin) {
            String oldPassword = Optional.ofNullable(passwordData.oldPassword())
                    .orElseThrow(() -> new IllegalArgumentException("Old password is required to change your own password"));

            String oldHash = userDetails.getPassword();
            if (!passwordEncoder.matches(oldPassword, oldHash)) {
                throw new AccessDeniedException("Old password does not match");
            }
        }

        String newPassword = passwordData.newPassword();

        String newHash = passwordEncoder.encode(newPassword);
        transactionTemplate.executeWithoutResult(status -> userRepo.setNewPassword(username, newHash));
    }


    @Override
    public void updateUser(UserDetails userDetails, String username, UserUpdateData userUpdateData) {
        specialNameValidator.validate(username);

        String firstName = userUpdateData.firstName();
        String lastName = userUpdateData.lastName();
        String email = userUpdateData.email();

        String myUsername = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!myUsername.equals(username) && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to update profile fields");
        }

        transactionTemplate.executeWithoutResult(status ->
                userRepo.updateUserFields(username,firstName,lastName,email));
    }

    @Override
    public void forcedDeleteRoles(Set<String> roles) {
        Set<String> normalizedRoles = normalizer.normalize(roles);

        int updated = Optional.ofNullable(transactionTemplate.execute(status ->
                        userRoleRepo.deleteAllRolesByNameIn(normalizedRoles)))
                .orElseThrow(() -> new RuntimeException("Failed to delete roles"));

        if (updated == 0) {
            throw new IllegalArgumentException("The roles were not deleted, and they probably did not exist");
        }
    }


}
