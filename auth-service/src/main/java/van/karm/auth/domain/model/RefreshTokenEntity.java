package van.karm.auth.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenEntity {

    @Id
    private String jti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private boolean revoked = false;

    public RefreshTokenEntity(String jti,
                              UserEntity user,
                              LocalDateTime issuedAt,
                              LocalDateTime expiresAt,
                              String deviceId,
                              boolean revoked) {
        this.jti = jti;
        this.user = user;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.deviceId = deviceId;
        this.revoked = revoked;
    }

}
