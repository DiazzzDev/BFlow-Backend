package Bflow.wallet.entities;

import Bflow.auth.entities.User;
import Bflow.wallet.enums.WalletRole;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "wallet_users")
public class WalletUser {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private WalletRole role; // OWNER, MEMBER
}
