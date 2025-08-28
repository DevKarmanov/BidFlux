package van.karm.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.auth.model.RefreshTokenEntity;
import van.karm.auth.model.UserEntity;
import van.karm.auth.repo.projection.UserAuthData;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByJtiAndRevokedFalse(String jti);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.user = :user AND r.deviceId = :deviceId")
    void deleteByUserAndDeviceId(@Param("user") UserEntity user, @Param("deviceId") String deviceId);

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
}
