package van.karm.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import van.karm.auth.model.UserEntity;
import van.karm.auth.repo.projection.UserIdProjection;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameIgnoreCase(String username);
    Boolean existsByUsernameIgnoreCase(String username);

}
