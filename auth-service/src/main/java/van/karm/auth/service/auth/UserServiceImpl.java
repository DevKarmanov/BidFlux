package van.karm.auth.service.auth;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import van.karm.auth.dto.Credentials;
import van.karm.auth.dto.response.Tokens;
import van.karm.auth.model.Role;
import van.karm.auth.model.UserEntity;
import van.karm.auth.repo.RefreshTokenRepo;
import van.karm.auth.repo.RoleRepo;
import van.karm.auth.repo.UserRepo;
import van.karm.auth.service.jwt.JwtService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;
    private final RefreshTokenRepo refreshTokenRepo;


    @Transactional
    @Override
    public Tokens login(String username, String password, String deviceId) {
        var user = userRepo.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Password does not match");
        }

        var roles = user.getRoles().stream()
                .map(Role::getAuthority)
                .toList();
        var accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), roles);

        refreshTokenRepo.deleteByUserAndDeviceId(user, deviceId);

        var refreshToken = jwtService.generateRefreshToken(user,deviceId);

        return new Tokens(accessToken, refreshToken);
    }


    @Transactional
    @Override
    public void register(String username, String password) {
        Credentials creds = normalizeAndValidateCredentials(username, password);

        if (userRepo.existsByUsernameIgnoreCase(creds.username())) {
            throw new EntityExistsException("User with this name already exists");
        }

        Role role = roleRepo.findByNameIgnoreCase("ROLE_USER");

        var user = UserEntity.builder()
                .username(creds.username())
                .password(passwordEncoder.encode(creds.password()))
                .roles(Set.of(role))
                .build();

        userRepo.save(user);
    }

    private Credentials normalizeAndValidateCredentials(String username, String password) {
        username = username.trim();
        password = password.trim();
        if (username.isEmpty() || password.isEmpty()) {
            throw new BadCredentialsException("Username or password cannot be empty");
        }
        return new Credentials(username, password);
    }
}
