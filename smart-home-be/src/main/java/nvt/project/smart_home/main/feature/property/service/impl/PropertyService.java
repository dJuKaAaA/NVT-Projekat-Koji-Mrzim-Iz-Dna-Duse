package nvt.project.smart_home.main.feature.property.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.dto.response.ImageResponseDto;
import nvt.project.smart_home.main.core.entity.City;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.mapper.ImageMapper;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.core.service.interf.IMailService;
import nvt.project.smart_home.main.core.service.interf.IUserService;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.dto.request.PropertyRequestDto;
import nvt.project.smart_home.main.feature.property.dto.request.PropertyStatusRequestDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyRefResponseDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyResponseDto;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.OwnerNotExistsException;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.mapper.PropertyMapper;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import nvt.project.smart_home.main.feature.property.service.interf.IPropertyService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service("PropertyService")
public class PropertyService implements IPropertyService {
    private final IUserService userService;
    private final PropertyRepository propertyRepository;
    private final IImageService imageService;
    private final IMailService mailService;
    private final ImageMapper imageMapper;

    @Override
    public PropertyResponseDto createRequest(PropertyRequestDto propertyRequestDto) throws IOException {
        Optional<UserEntity> owner= userService.findByEmail(propertyRequestDto.getOwnerEmail());
        if (owner.isEmpty()) throw new OwnerNotExistsException();

        Property property = Property.builder()
                .name(propertyRequestDto.getName())
                .owner(owner.get())
                .floors(propertyRequestDto.getFloors())
                .area(propertyRequestDto.getArea())
                .longitude(propertyRequestDto.getLongitude())
                .latitude(propertyRequestDto.getLatitude())
                .address(propertyRequestDto.getAddress())
                .city(new City(propertyRequestDto.getCity().getId(),
                        propertyRequestDto.getCity().getName(),
                        propertyRequestDto.getCity().getCountryId(),
                        propertyRequestDto.getCity().getCountryName()))
                .type(propertyRequestDto.getType())
                .status(PropertyStatus.PENDING)
                .build();


        ImageRequestDto image = propertyRequestDto.getImage();
        property.setImageFormat(image.getFormat());
        property = propertyRepository.save(property);
        image.setName(property.getOwner().getEmail() + "-" + property.getId());
        imageService.savePropertyImageToFileSystem(image);

        return PropertyMapper.INSTANCE.propertyToPropertyResponseDto(property);
    }

    @Override
    public List<PropertyResponseDto> getAllByOwnerEmail(String email) throws IOException {
        Optional<UserEntity> owner= userService.findByEmail(email);
        if (owner.isEmpty()) throw new OwnerNotExistsException();
        Optional<List<Property>> properties = propertyRepository.findByOwner_Email(email);
        List<PropertyResponseDto> responseDtos = new ArrayList<>();
        if (properties.isEmpty()) return responseDtos;
        for (Property property : properties.get()) responseDtos.add(getResponseDto(property));
        return responseDtos;
    }

    @Override
    public List<PropertyResponseDto> getAllRequests() throws IOException {
        Optional<List<Property>> properties = propertyRepository.findByStatus(PropertyStatus.PENDING);
        List<PropertyResponseDto> responseDtos = new ArrayList<>();
        if (properties.isEmpty()) return responseDtos;
        for (Property property : properties.get()) responseDtos.add(getResponseDto(property));
        return responseDtos;
    }

    @Override
    public void changeStatus(PropertyStatusRequestDto propertyRequest) throws IOException {
        Optional<Property> property = propertyRepository.findById(propertyRequest.getId());
        if(property.isEmpty()) throw new PropertyNotExistsException();
        if(propertyRequest.getIsApproved()) approveRequest(property.get());
        else denyRequest(property.get(), propertyRequest.getDenialReason());
    }

    private void approveRequest(Property property) throws IOException {
        property.setStatus(PropertyStatus.APPROVED);
        propertyRepository.save(property);
        Map<String, Object> model = new HashMap<>();
        model.put("propertyName", property.getName());
        mailService.sendApproveOrDenyPropertyEmail("Property Approved", property.getOwner().getEmail(), model, "approve-property");
    }

    private void denyRequest(Property property, String reason) throws IOException {
        property.setStatus(PropertyStatus.DENIED);
        property.setDenialReason(reason);
        propertyRepository.save(property);
        Map<String, Object> model = new HashMap<>();
        model.put("propertyName", property.getName());
        model.put("reason", property.getDenialReason());
        mailService.sendApproveOrDenyPropertyEmail("Property Denied", property.getOwner().getEmail(), model, "deny-property");
    }

    private PropertyResponseDto getResponseDto(Property property) throws IOException{
        PropertyResponseDto responseDto = PropertyMapper.INSTANCE.propertyToPropertyResponseDto(property);
        ImageResponseDto image = imageService.readPropertyImageFromFileSystem(property.getOwner().getEmail() + "-" + property.getId(), property.getImageFormat());
        responseDto.setImage(imageMapper.responseToRequest(image));
        return responseDto;
    }

    @Override
    public Optional<Property> getById(long propertyId) {
        return propertyRepository.findById(propertyId);
    }

    @SneakyThrows
    @Override
    public List<PropertyRefResponseDto> getAll() {
        List<Property> allProperties = propertyRepository.findAll();
        return allProperties.stream().map(property -> {
            return   PropertyRefResponseDto
                    .builder()
                    .id(property.getId())
                    .address(property.getAddress())
                    .name(property.getAddress())
                    .build();
        }).toList();
    }

}