package van.karm.auth.infrastructure.service.auth;

import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auth.application.service.auth.AuthService;
import van.karm.auth.application.service.jwt.access.AccessTokenService;
import van.karm.auth.application.service.jwt.refresh.RefreshTokenService;
import van.karm.auth.domain.repo.UserRepo;
import van.karm.auth.infrastructure.validator.Validator;
import van.karm.auth.presentation.dto.request.UserRegData;
import van.karm.auth.presentation.dto.response.token.RefreshToken;
import van.karm.auth.presentation.dto.response.token.Tokens;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepo userRepo;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final Validator specialNameValidator;

    public AuthServiceImpl(UserRepo userRepo,
                           AccessTokenService accessTokenService,
                           RefreshTokenService refreshTokenService,
                           PasswordEncoder passwordEncoder,
                           TransactionTemplate transactionTemplate,
                           @Qualifier("special-name-validator") Validator specialNameValidator) {
        this.userRepo = userRepo;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
        this.specialNameValidator = specialNameValidator;
    }

    @Override
    public Tokens login(String username, String password, String deviceId) {
        specialNameValidator.validate(username);

        var user = userRepo.findProjectionByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getEnabled()){
            throw new DisabledException(user.getBlockReason());
        }
        if (user.getDeleted()){
            throw new AccessDeniedException("The account has been deleted");
        }

        UUID userId = user.getId();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Password does not match");
        }

        var roles = userRepo.findRoleNamesByUsername(username);

        log.debug("Генерация access-токена для пользователя '{}'", username);
        var accessToken = accessTokenService.generate(username, userId, roles);

        log.debug("Генерация refresh-токена для пользователя '{}', устройство '{}'", username, deviceId);
        RefreshToken refreshToken = transactionTemplate.execute(status ->
                refreshTokenService.generate(userId, username, deviceId)
        );

        return new Tokens(accessToken, refreshToken);
    }

    @Override
    public void register(UserRegData userRegData) {
        UUID userId = UUID.randomUUID();
        String password = userRegData.password();
        String username = userRegData.username();
        String email = userRegData.email();
        String firstName = userRegData.firstName();
        String lastName = userRegData.lastName();

        specialNameValidator.validate(username);

        String encodedPassword = passwordEncoder.encode(password);
        log.debug("Пароль пользователя '{}' успешно захеширован", username);

        transactionTemplate.executeWithoutResult(status -> {
            int inserted = userRepo.insertUserIfNotExists(userId,username,encodedPassword,email,firstName,lastName);

            if (inserted > 0) {
                userRepo.insertUserRoleByName(userId, "ROLE_USER");
                log.info("Пользователю '{}' назначена роль ROLE_USER", username);
            } else {
                log.debug("Пользователь '{}' уже существует, регистрация отклонена", username);
                throw new EntityExistsException("User already exists");
            }
        });
    }

}
