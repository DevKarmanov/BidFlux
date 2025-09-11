package van.karm.auth.infrastructure.service.auth;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auth.application.service.jwt.access.AccessTokenService;
import van.karm.auth.application.service.jwt.refresh.RefreshTokenService;
import van.karm.auth.application.service.auth.AuthService;
import van.karm.auth.domain.repo.UserRepo;
import van.karm.auth.presentation.dto.response.token.RefreshToken;
import van.karm.auth.presentation.dto.response.token.Tokens;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepo userRepo;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Tokens login(String username, String password, String deviceId) {
        var user = userRepo.findProjectionByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

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
    public void register(String username, String password) {
        UUID userId = UUID.randomUUID();

        String encodedPassword = passwordEncoder.encode(password);
        log.debug("Пароль пользователя '{}' успешно захеширован", username);

        transactionTemplate.executeWithoutResult(status -> {
            int inserted = userRepo.insertUserIfNotExists(userId,username, encodedPassword);

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
