package Bflow.Wallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBWALLET")
@Getter @Setter @ToString @EqualsAndHashCode
public class EntityWallet {
    @Id @Column(name = "IDWALLET")
    private Long idWallet;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "VALUE", nullable = false)
    private Double value;

    @Column(name = "INITIALVALUE")
    private Double initialValue;
}
