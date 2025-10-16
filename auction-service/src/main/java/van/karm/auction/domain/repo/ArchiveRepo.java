package van.karm.auction.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import van.karm.auction.domain.model.Archive;

import java.util.UUID;

public interface ArchiveRepo extends JpaRepository<Archive, UUID> {
    @Query(value = "SELECT username FROM users WHERE id=:userId", nativeQuery = true)
    String getUserNameByUserId(UUID userId);

    @Query(value = "SELECT id FROM users WHERE username=:username", nativeQuery = true)
    UUID getUserIdByUsername(String username);

    int deleteArchiveById(UUID id);
}
