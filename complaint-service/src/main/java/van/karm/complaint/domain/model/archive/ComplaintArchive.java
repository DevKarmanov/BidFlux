package van.karm.complaint.domain.model.archive;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import van.karm.complaint.domain.model.ComplaintReason;
import van.karm.complaint.domain.model.ComplaintStatus;
import van.karm.complaint.domain.model.ComplaintTargetType;
import van.karm.complaint.domain.model.complaint.Complaint;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "complaints_archive")
@NoArgsConstructor
@Getter
public class ComplaintArchive {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ComplaintTargetType targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ComplaintReason reason;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ComplaintStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    private UUID resolvedBy;

    @Column(columnDefinition = "text")
    private String moderatorComment;

    public ComplaintArchive(Complaint complaint, String moderatorComment, UUID resolvedBy, ComplaintStatus status) {
        this.id = complaint.getId();
        this.authorId = complaint.getAuthorId();
        this.targetType = complaint.getTargetType();
        this.targetId = complaint.getTargetId();
        this.reason = complaint.getReason();
        this.description = complaint.getDescription();
        this.status = status;
        this.createdAt = complaint.getCreatedAt();
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolvedBy;
        this.moderatorComment = moderatorComment;
    }
}