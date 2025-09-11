package van.karm.auth.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.auth.domain.model.UserEntity;
import van.karm.auth.domain.repo.projection.UserIdAndPasswordData;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsernameIgnoreCaseWithRoles(@Param("username") String username);

    Optional<UserIdAndPasswordData> findProjectionByUsernameIgnoreCase(String username);

    @Query("""
    SELECT r.name
    FROM UserEntity u
    JOIN u.roles r
    WHERE u.username = :username
    """)
    Set<String> findRoleNamesByUsername(@Param("username") String username);


    @Modifying
    @Query(value = """
INSERT INTO users(id, username, password, enabled)
VALUES (:id, :username, :password, true)
ON CONFLICT (LOWER(username)) DO NOTHING
""", nativeQuery = true)
    int insertUserIfNotExists(
            @Param("id") UUID id,
            @Param("username") String username,
            @Param("password") String password);

    @Modifying
    @Query(value = """
    INSERT INTO users_roles(user_id, role_id)
    SELECT :userId, r.id
    FROM roles r
    WHERE LOWER(r.name) = LOWER(:roleName)
    ON CONFLICT (user_id, role_id) DO NOTHING
    """, nativeQuery = true)
    void insertUserRoleByName(@Param("userId") UUID userId, @Param("roleName") String roleName);

    @Query(value = "SELECT u.username FROM UserEntity u WHERE u.id = :id")
    Optional<String> findUsernameById(UUID id);
}
