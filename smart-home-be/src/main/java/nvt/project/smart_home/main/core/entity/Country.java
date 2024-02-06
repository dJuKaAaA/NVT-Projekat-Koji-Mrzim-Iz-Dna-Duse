package nvt.project.smart_home.main.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "countries")
@Entity
@Builder
public class Country {
    @Id @GeneratedValue
    private Long id;
    private String name;
    public Country(String name) {
        this.name = name;
    }
}
