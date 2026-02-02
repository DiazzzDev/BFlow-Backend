package Bflow.wallet.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter @Setter @ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String currency; // "USD", "EUR"

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    private BigDecimal initialValue;

    @CreationTimestamp
    private Instant createdAt;
}
