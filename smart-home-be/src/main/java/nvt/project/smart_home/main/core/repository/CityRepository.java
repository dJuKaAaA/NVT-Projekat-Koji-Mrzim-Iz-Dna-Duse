package nvt.project.smart_home.main.core.repository;

import nvt.project.smart_home.main.core.entity.City;
import nvt.project.smart_home.main.core.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findCityByNameAndAndCountry(String name, Country country);

}
