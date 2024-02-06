package nvt.project.smart_home.main.core.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.entity.City;
import nvt.project.smart_home.main.core.entity.Country;
import nvt.project.smart_home.main.core.repository.CityRepository;
import nvt.project.smart_home.main.core.repository.CountryRepository;
import nvt.project.smart_home.main.core.service.interf.IDataInitializerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DataInitializerService implements IDataInitializerService {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CityRepository cityRepository;
    @Value("${directory.path.data.cities.countries}")
    private final String directoryPathOfCitiesCountriesData;

    @Override @PostConstruct
    public void initializeCitiesCountriesData() throws IOException {
        Gson gson = new Gson();
        Reader reader = new FileReader(directoryPathOfCitiesCountriesData);
        Map<String, String[]> countryCityMap = gson.fromJson(reader, new TypeToken<Map<String, String[]>>() {}.getType());
        for (Map.Entry<String, String[]> entry : countryCityMap.entrySet()) {
            String countryName = entry.getKey();
            String[] cities = entry.getValue();

            Optional<Country> existedCountry = countryRepository.findCountryByName(countryName);
            Country country = existedCountry.orElseGet(() -> countryRepository.save(new Country(countryName)));

            for (String city : cities) {
                Optional<City> existedCity = cityRepository.findCityByNameAndAndCountry(city, country);
                if(existedCity.isEmpty()) cityRepository.save(new City(city, country));
            }
        }
    }
}