package bflow.auth.entities;

import bflow.auth.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a user's authentication method and credentials.
 */
@Entity
@Table(name = "auth_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthAccount {

    /** The unique identifier for the authentication account. */
    @Id
    @GeneratedValue
    private UUID id;

    /** The user associated with this authentication account. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /** The provider used (e.g., LOCAL, GOOGLE). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    /** The external provider's unique user ID, if applicable. */
    @Column
    private String providerUserId;

    /** The hashed password for local authentication. */
    @Column
    private String passwordHash;

    /** Whether this user is currently active. */
    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    /** Timestamp of when the account was first created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Automatically sets the creation timestamp before persisting.
     */
    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}