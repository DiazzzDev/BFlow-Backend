package Bflow.auth.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        }
)
@Getter @Setter @ToString @EqualsAndHashCode
public class Role {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;
}
