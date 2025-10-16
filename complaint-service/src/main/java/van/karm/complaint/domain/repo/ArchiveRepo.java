package van.karm.complaint.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import van.karm.complaint.domain.model.archive.ComplaintArchive;

import java.util.UUID;

public interface ArchiveRepo extends JpaRepository<ComplaintArchive, UUID> {
}
