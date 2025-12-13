package Bflow.Wallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TBWALLET")
public class EntityWallet {
    @Id @Column(name = "idWallet")
    private Long idWallet;
}
