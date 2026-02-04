package bflow.wallet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entity representing a financial wallet.
 */
@Entity
@Table(name = "wallets")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    /** The unique identifier for the wallet. */
    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    /** The display name of the wallet. */
    @Column(nullable = false)
    private String name;

    /** The currency code (e.g., USD, EUR). */
    @Column(nullable = false)
    private String currency; // "USD", "EUR"

    /** The current available balance. */
    @Column(nullable = false)
    private BigDecimal balance;

    /** The balance the wallet started with. */
    @Column(nullable = false, updatable = false)
    private BigDecimal initialValue;

    /** The timestamp when the wallet was created. */
    @CreationTimestamp
    private Instant createdAt;
}
