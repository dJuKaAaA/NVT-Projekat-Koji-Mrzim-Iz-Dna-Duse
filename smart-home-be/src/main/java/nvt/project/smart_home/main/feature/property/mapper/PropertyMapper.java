package nvt.project.smart_home.main.feature.property.mapper;

import nvt.project.smart_home.main.feature.property.dto.response.PropertyResponseDto;
import nvt.project.smart_home.main.feature.property.entity.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PropertyMapper {
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "ownerEmail", source = "property.owner.email"),
            @Mapping(target = "city", source = "property.city"),
            @Mapping(target = "city.countryId", source = "property.city.country.id"),
            @Mapping(target = "city.countryName", source = "property.city.country.name")
    })
    PropertyResponseDto propertyToPropertyResponseDto(Property property);

    List<PropertyResponseDto> propertiesToPropertyResponseDtos(List<Property> properties);
}
