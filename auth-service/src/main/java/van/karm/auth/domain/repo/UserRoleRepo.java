package van.karm.auth.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.auth.domain.model.Role;

import java.util.Set;
import java.util.UUID;

public interface UserRoleRepo extends JpaRepository<Role,Long> {


    @Modifying
    @Query(
            value = """
        DELETE FROM users_roles
        WHERE user_id = :userId
          AND role_id IN (SELECT id FROM roles WHERE name IN :roleNames)
    """,
            nativeQuery = true
    )
    int removeRolesFromUser(@Param("userId") UUID userId, @Param("roleNames") Set<String> roleNames);

    @Modifying
    @Query(
            value = """
        INSERT INTO roles (name)
        SELECT v.role_name
        FROM (VALUES (:roleNames)) AS v(role_name)
        WHERE NOT EXISTS (
            SELECT 1 FROM roles r
            WHERE r.name = v.role_name
        )
    """,
            nativeQuery = true
    )
    void createMissingRoles(@Param("roleNames") Set<String> roleNames);


    @Modifying
    @Query(
            value = """
        INSERT INTO users_roles (user_id, role_id)
        SELECT :userId, r.id
        FROM roles r
        WHERE r.name IN :roleNames
          AND NOT EXISTS (
              SELECT 1 FROM users_roles ur
              WHERE ur.user_id = :userId AND ur.role_id = r.id
    )
    """,
            nativeQuery = true
    )
    int addRolesToUser(@Param("userId") UUID userId, @Param("roleNames") Set<String> roleNames);

    int deleteAllRolesByNameIn(Set<String> normalizedRoles);
}
