package Diaz.Dev.BFlow.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TBWALLET")
@Getter @Setter @EqualsAndHashCode
public class EntityWallet {
    @Id @Column(name = "idWallet", columnDefinition = "SERIAL", insertable = false, updatable = false)
    private Long idWallet;
}
