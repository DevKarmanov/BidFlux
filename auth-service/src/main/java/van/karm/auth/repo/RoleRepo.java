package van.karm.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import van.karm.auth.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
