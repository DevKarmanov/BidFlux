package van.karm.auth.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import van.karm.auth.config.properties.JwtProperties;
import van.karm.auth.dto.response.AccessToken;
import van.karm.auth.dto.response.RefreshToken;
import van.karm.auth.model.RefreshTokenEntity;
import van.karm.auth.model.UserEntity;
import van.karm.auth.repo.RefreshTokenRepo;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtProperties props;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtService(JwtProperties props, RefreshTokenRepo refreshTokenRepo) throws Exception {
        this.props = props;
        this.refreshTokenRepo = refreshTokenRepo;
        this.privateKey = loadPrivateKey(props.privateKeyPath());
        this.publicKey = loadPublicKey(props.publicKeyPath());
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        Resource resource = new ClassPathResource(path);
        String key = Files.readString(resource.getFile().toPath())
                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        Resource resource = new ClassPathResource(path);
        String key = Files.readString(resource.getFile().toPath())
                .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public AccessToken generateAccessToken(String subject, UUID userId, Collection<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.accessTtlMin(), ChronoUnit.MINUTES);

        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(props.issuer())
                .setAudience(props.audience())
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .claim("userId",userId)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        long expiresInSeconds = ChronoUnit.SECONDS.between(now, exp);
        return new AccessToken(token, "Bearer", expiresInSeconds);
    }


    @Transactional
    public AccessToken generateAccessTokenFromRefresh(String refreshJwt) {
        var claims = parse(refreshJwt);
        var jti = claims.getId();
        var userId = UUID.fromString(claims.get("userId", String.class));

        var projection = refreshTokenRepo.findUserAuthByValidJti(jti)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found or invalid"));

        return generateAccessToken(projection.getUsername(), userId, projection.getRoles());
    }

    @Transactional
    public RefreshToken generateRefreshToken(UserEntity user, String deviceId) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.refreshTtlDays(), ChronoUnit.DAYS);

        String jti = UUID.randomUUID().toString();

        var refreshTokenEntity = new RefreshTokenEntity(
                jti,
                user,
                LocalDateTime.ofInstant(now, ZoneOffset.UTC),
                LocalDateTime.ofInstant(exp, ZoneOffset.UTC),
                deviceId,
                false);


        refreshTokenRepo.save(refreshTokenEntity);

        return new RefreshToken(
                jti,
                LocalDateTime.ofInstant(now, ZoneOffset.UTC),
                LocalDateTime.ofInstant(exp, ZoneOffset.UTC),
                generateRefreshTokenJwt(user.getUsername(),jti, user.getId())
        );
    }

    public String generateRefreshTokenJwt(String userName, String jti, UUID userId) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.refreshTtlDays(), ChronoUnit.DAYS);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(props.issuer())
                .setAudience(props.audience())
                .setSubject(userName)
                .setId(jti)
                .claim("userId", userId)
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }


    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(props.issuer())
                .requireAudience(props.audience())
                .setAllowedClockSkewSeconds(30)
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
