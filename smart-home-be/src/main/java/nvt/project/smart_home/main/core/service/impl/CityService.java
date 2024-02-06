package nvt.project.smart_home.main.core.service.impl;

import nvt.project.smart_home.main.core.dto.CityDto;
import nvt.project.smart_home.main.core.entity.City;
import nvt.project.smart_home.main.core.repository.CityRepository;
import nvt.project.smart_home.main.core.service.interf.ICityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("CityService")
public class CityService implements ICityService {
    @Autowired
    CityRepository cityRepository;
    @Override
    public List<CityDto> getAll() {
        List<City> cities = cityRepository.findAll();
        return cities.stream().map(CityDto::new).collect(Collectors.toList());
    }
}
