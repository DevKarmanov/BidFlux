package van.karm.auth.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.auth.domain.model.UserEntity;
import van.karm.auth.domain.repo.projection.UserForLoginData;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepo extends JpaRepository<UserEntity, UUID> {
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsernameIgnoreCaseWithRoles(@Param("username") String username);

    Optional<UserForLoginData> findProjectionByUsernameIgnoreCase(String username);

    @Query("""
    SELECT r.name
    FROM UserEntity u
    JOIN u.roles r
    WHERE u.username = :username
    """)
    Set<String> findRoleNamesByUsername(@Param("username") String username);


    @Modifying
    @Query(
            value = """
        INSERT INTO users (id, username, password, email, first_name, last_name, enabled)
        VALUES (:id, :username, :password, :email, :firstName, :lastName, true)
        ON CONFLICT (LOWER(username)) DO NOTHING
        """,
            nativeQuery = true
    )
    int insertUserIfNotExists(
            @Param("id") UUID id,
            @Param("username") String username,
            @Param("password") String password,
            @Param("email") String email,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName
    );

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

    @Modifying
    @Query(
            value = """
        UPDATE users
        SET username = CONCAT('DELETED_', id::varchar),
            deleted = true
        WHERE id IN (:userIds)
    """,
            nativeQuery = true
    )
    void markUsersDeleted(@Param("userIds") Set<UUID> userIds);

    @Modifying
    @Query(
            value = """
        DELETE FROM auction_allowed_users
        WHERE user_id IN (:userIds)
    """,
            nativeQuery = true
    )
    void deleteAllowedUsers(@Param("userIds") Set<UUID> userIds);



    @Query(value = """
    SELECT NOT EXISTS (
        SELECT 1
        FROM archived_auctions aa
        WHERE aa.winner_id = :userId
    )
    AND NOT EXISTS (
        SELECT 1
        FROM auction a
        WHERE a.winner_id = :userId
    )
""", nativeQuery = true)
    boolean userWasWinner(@Param("userId") UUID userId);


    @Query(value = """
    SELECT NOT EXISTS (
        SELECT 1
        FROM auction a
        WHERE a.owner_id = :userId
          AND a.status <> 'INACTIVE'
    )
    AND NOT EXISTS (
        SELECT 1
        FROM archived_auctions aa
        WHERE aa.owner_id = :userId
    )
""", nativeQuery = true)
    boolean hasNoActiveOrArchivedAuctions(@Param("userId") UUID userId);

    @Modifying
    @Query(value = "UPDATE users SET last_online = NOW() WHERE id = :userId", nativeQuery = true)
    void updateUserLastOnlineState(@Param("userId") UUID userId);


    @Query(value = """
    SELECT u.id
    FROM users u
    WHERE u.deleted = false
      AND u.last_online <= NOW() - INTERVAL '1 year'
      AND NOT EXISTS (
          SELECT 1
          FROM auction a
          WHERE a.owner_id = u.id
            AND a.status = 'ACTIVE'
      )
      AND NOT EXISTS (
          SELECT 1
          FROM archived_auctions aa
          WHERE aa.owner_id = u.id
      )
      AND NOT EXISTS (
          SELECT 1
          FROM archived_auctions aa
          WHERE aa.winner_id = u.id
      );
""", nativeQuery = true)
    Set<UUID> findInactiveUsers();

    @Query("SELECT u.id FROM UserEntity u WHERE u.enabled = false AND u.lastOnline < :dateTime AND u.deleted = false")
    Set<UUID> findIdsBannedBefore(@Param("dateTime") LocalDate dateTime);

    @Modifying
    @Query("UPDATE UserEntity u SET u.enabled = :enabled, u.blockReason = :reason WHERE u.username = :username")
    void updateUserStatus(@Param("username") String username,
                         @Param("enabled") boolean enabled,
                         @Param("reason") String reason);


    @Query("select u.enabled from UserEntity u where u.username = :username")
    Optional<Boolean> getUserEnabledByUsername(@Param("username") String username);

    @Query("SELECT u.id from UserEntity u WHERE u.username = :username")
    Optional<UUID> getUserIdByUsername(@Param("username") String username);

    @Modifying
    @Query(value = """
    DELETE FROM auction
    WHERE owner_id IN (:userIds)
""", nativeQuery = true)
    void deleteUserAuctions(@Param("userIds") Set<UUID> userIds);

    @Modifying
    @Query(value = """
    DELETE FROM archived_auctions
    WHERE owner_id IN (:userIds)
""", nativeQuery = true)
    void deleteUserArchivedAuctions(@Param("userIds") Set<UUID> userIds);

    @Modifying
    @Query("UPDATE UserEntity u SET u.password = :newPassword WHERE u.username = :username")
    void setNewPassword(String username, String newPassword);

    @Modifying
    @Query("""
    UPDATE UserEntity u
    SET u.firstName = COALESCE(TRIM(:firstName), u.firstName),
        u.lastName  = COALESCE(TRIM(:lastName), u.lastName),
        u.email     = COALESCE(TRIM(:email), u.email)
    WHERE u.username = :username
""")
    void updateUserFields(@Param("username") String username,
                         @Param("firstName") String firstName,
                         @Param("lastName") String lastName,
                         @Param("email") String email);

}
