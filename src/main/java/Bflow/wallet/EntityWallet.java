package Bflow.wallet;

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
    @Id @Column(name = "idWallet")
    private Long idWallet;
}
