package van.karm.complaint.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.domain.model.complaint.Complaint;
import van.karm.complaint.domain.repo.projection.UserEnableAndBockMessage;

import java.util.UUID;

public interface ComplaintRepo extends JpaRepository<Complaint, UUID> {
    @Query(value = """
    SELECT enabled AS isEnable, block_reason AS blockMessage 
    FROM users 
    WHERE id = :id
""", nativeQuery = true)
    UserEnableAndBockMessage findUserEnableAndBockMessageById(@Param("id") UUID id);

    @Query(value = """
    SELECT u.username FROM users u WHERE u.id = :authorId
""", nativeQuery = true)
    String getUsernameByAuthorId(UUID authorId);

    @Query(value = "SELECT u.id FROM users u WHERE u.username = :username", nativeQuery = true)
    UUID getAuthorIdByUsername(String username);

    @Query(value = """
    SELECT a.title FROM auction a WHERE a.id = :targetId
""", nativeQuery = true)
    String getAuctionTitleById(UUID targetId);

    @Query(value = """
    SELECT a.title FROM archived_auctions a WHERE a.id = :targetId
""", nativeQuery = true)
    String getArchivedAuctionTitleById(UUID targetId);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM auction
        WHERE id = :targetId
    )
""", nativeQuery = true)
    boolean existsAuctionById(@Param("targetId") UUID targetId);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM users
        WHERE id = :targetId
    )
""", nativeQuery = true)
    boolean existsUserById(@Param("targetId") UUID targetId);

    boolean existsByAuthorIdAndTargetIdAndTargetType(UUID authorId, UUID targetId, ComplaintTargetType targetType);

}
