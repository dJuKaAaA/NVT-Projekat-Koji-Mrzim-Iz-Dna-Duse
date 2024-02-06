package nvt.project.smart_home.main.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nvt.project.smart_home.main.core.entity.City;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {
    private Long id;
    private String name;
    private Long countryId;
    private String countryName;
    public CityDto(City city) {
        this.id = city.getId();
        this.name = city.getName();
        this.countryId = city.getCountry().getId();
        this.countryName = city.getCountry().getName();
    }
}
