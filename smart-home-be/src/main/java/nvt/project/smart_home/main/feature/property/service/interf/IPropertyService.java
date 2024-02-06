package nvt.project.smart_home.main.feature.property.service.interf;

import nvt.project.smart_home.main.feature.property.dto.request.PropertyRequestDto;
import nvt.project.smart_home.main.feature.property.dto.request.PropertyStatusRequestDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyRefResponseDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyResponseDto;
import nvt.project.smart_home.main.feature.property.entity.Property;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IPropertyService {
    PropertyResponseDto createRequest(PropertyRequestDto property) throws IOException;
    List<PropertyResponseDto> getAllByOwnerEmail(String email) throws IOException;
    List<PropertyResponseDto> getAllRequests() throws IOException;
    void changeStatus(PropertyStatusRequestDto propertyRequest) throws IOException;

    Optional<Property> getById(long propertyId);
    List<PropertyRefResponseDto> getAll();
}