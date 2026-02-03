package bflow.wallet.entities;

import bflow.auth.entities.User;
import bflow.wallet.enums.WalletRole;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Mapping entity between Wallets and Users.
 */
@Entity
@Table(name = "wallet_users")
public class WalletUser {

    /** Unique identifier for the relationship record. */
    @Id
    @GeneratedValue
    private UUID id;

    /** The wallet associated with this record. */
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    /** The user associated with this record. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The role the user has in this wallet. */
    @Enumerated(EnumType.STRING)
    private WalletRole role; // OWNER, MEMBER
}
