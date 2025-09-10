package van.karm.auth.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.auth.domain.model.RefreshTokenEntity;
import van.karm.auth.domain.repo.projection.UserAuthData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByJtiAndRevokedFalse(String jti);

    @Query("""
    SELECT u.username AS username, r.name AS roles
    FROM RefreshTokenEntity t
    JOIN t.user u
    JOIN u.roles r
    WHERE t.jti = :jti
      AND t.revoked = false
      AND t.expiresAt > CURRENT_TIMESTAMP
""")
    List<UserAuthData> findUserAuthByValidJti(@Param("jti") String jti);

    @Modifying
    @Query(
            value = """
INSERT INTO refresh_tokens(jti, user_id, device_id, issued_at, expires_at, revoked)
VALUES (:jti, :userId, :deviceId, :issued, :expires, false)
ON CONFLICT (user_id, device_id)
DO UPDATE SET jti = EXCLUDED.jti, issued_at = EXCLUDED.issued_at, expires_at = EXCLUDED.expires_at, revoked = false
""",
            nativeQuery = true
    )
    void upsertRefreshToken(@Param("jti") String jti,
                            @Param("userId") UUID userId,
                            @Param("deviceId") String deviceId,
                            @Param("issued") LocalDateTime issued,
                            @Param("expires") LocalDateTime expires);

}
