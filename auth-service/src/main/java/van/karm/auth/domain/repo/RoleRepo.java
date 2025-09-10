package van.karm.auth.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import van.karm.auth.domain.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
