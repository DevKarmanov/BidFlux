package van.karm.auth.service.auth;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import van.karm.auth.dto.Credentials;
import van.karm.auth.dto.response.RefreshToken;
import van.karm.auth.dto.response.Tokens;
import van.karm.auth.repo.UserRepo;
import van.karm.auth.service.jwt.JwtService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Tokens login(String username, String password, String deviceId) {
        log.debug("Попытка входа: {}", username);

        var user = userRepo.findProjectionByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UUID userId = user.getId();
        log.debug("Пользователь '{}' найден, проверка пароля...", username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.debug("Пароль не совпадает для пользователя '{}'", username);
            throw new BadCredentialsException("Password does not match");
        }

        log.debug("Пароль подтверждён. Получение ролей пользователя '{}'", username);
        var roles = userRepo.findRoleNamesByUsername(username);

        log.debug("Генерация access-токена для пользователя '{}'", username);
        var accessToken = jwtService.generateAccessToken(username, userId, roles);

        log.debug("Генерация refresh-токена для пользователя '{}', устройство '{}'", username, deviceId);
        RefreshToken refreshToken = transactionTemplate.execute(status ->
                jwtService.generateRefreshToken(userId, username, deviceId)
        );

        log.info("Вход выполнен успешно: {}", username);
        return new Tokens(accessToken, refreshToken);
    }

    @Override
    public void register(String username, String password) {
        log.debug("Регистрация нового пользователя: {}", username);

        Credentials creds = normalizeAndValidateCredentials(username, password);

        UUID userId = UUID.randomUUID();
        log.debug("Сгенерирован UUID для пользователя '{}': {}", creds.username(), userId);

        String encodedPassword = passwordEncoder.encode(creds.password());
        log.debug("Пароль пользователя '{}' успешно захеширован", creds.username());

        transactionTemplate.executeWithoutResult(status -> {
            log.debug("Попытка вставки пользователя '{}' в базу", creds.username());
            int inserted = userRepo.insertUserIfNotExists(userId, creds.username(), encodedPassword);

            if (inserted > 0) {
                log.debug("Пользователь '{}' успешно добавлен. Назначение роли...", creds.username());
                userRepo.insertUserRoleByName(userId, "ROLE_USER");
                log.info("Пользователю '{}' назначена роль ROLE_USER", creds.username());
            } else {
                log.debug("Пользователь '{}' уже существует, регистрация отклонена", creds.username());
                throw new EntityExistsException("User already exists");
            }
        });
    }

    private Credentials normalizeAndValidateCredentials(String username, String password) {
        username = username.trim();
        password = password.trim();
        log.debug("Валидация учетных данных: username='{}', password длина={}", username, password.length());
        if (username.isEmpty() || password.isEmpty()) {
            throw new BadCredentialsException("Username or password cannot be empty");
        }
        return new Credentials(username, password);
    }
}
