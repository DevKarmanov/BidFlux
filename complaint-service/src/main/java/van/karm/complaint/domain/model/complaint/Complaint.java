package van.karm.complaint.domain.model.complaint;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import van.karm.complaint.domain.model.ComplaintReason;
import van.karm.complaint.domain.model.ComplaintStatus;
import van.karm.complaint.domain.model.ComplaintTargetType;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "complaints")
@NoArgsConstructor
@Getter
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    @Column(nullable = false)
    @Setter
    private ComplaintStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Complaint(UUID authorId,
                     ComplaintTargetType targetType,
                     UUID targetId,
                     ComplaintReason reason,
                     String description) {
        this.authorId = authorId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.description = description;
        this.status = ComplaintStatus.IN_PROGRESS;
        this.createdAt = LocalDateTime.now();
    }
}
