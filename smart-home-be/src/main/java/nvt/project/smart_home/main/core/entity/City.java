package nvt.project.smart_home.main.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
@Entity
@Builder
public class City {
    @Id @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private Country country;
    public City(String name, Country country) {
        this.name = name;
        this.country = country;
    }
    public City(Long id, String name, Long countryId, String countryName) {
        this.id = id;
        this.name = name;
        this.country = new Country(countryId, countryName);
    }
}
