package nvt.project.smart_home.main.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.dto.response.ImageResponseDto;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.mapper.ImageMapper;
import nvt.project.smart_home.main.core.mapper.SmartDeviceMapper;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.core.service.interf.ISmartDeviceService;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SmartDeviceService implements ISmartDeviceService {

    private final SmartDeviceRepository smartDeviceRepository;
    private final SmartDeviceMapper smartDeviceMapper;
    private final ImageMapper imageMapper;
    private final IImageService imageService;
    private final PropertyRepository propertyRepository;

    @Override
    public SmartDeviceResponseDto getById(Long id) throws IOException {
        SmartDeviceEntity entity = smartDeviceRepository.findById(id).orElseThrow(SmartDeviceNotFoundException::new);
        SmartDeviceResponseDto response = smartDeviceMapper.entityToResponseDto(entity);
        response.setImage(ImageResponseDto.builder()
                .name(entity.getName() + entity.getProperty().getId())
                .format(entity.getImageFormat())
                .build());
        return response;
    }

    @Override
    public Collection<SmartDeviceResponseDto> getByPropertyId(Long propertyId) {
        Collection<SmartDeviceEntity> devices = smartDeviceRepository.findAllByPropertyId(propertyId);
        return devices.stream()
                .map(device -> {
                    SmartDeviceResponseDto response = smartDeviceMapper.entityToResponseDto(device);
                    response.setImage(ImageResponseDto.builder()
                            .name(device.getName() + device.getProperty().getId())
                            .format(device.getImageFormat())
                            .build());
                    return response;
                })
                .toList();
    }

    @SneakyThrows
    public ImageResponseDto getImage(SmartDeviceEntity entity) {
        String imageName = entity.getName() + entity.getId();
        String format = entity.getImageFormat();
        return ImageResponseDto.builder()
                .name(imageName)
                .format(format)
                .build();
    }

    @Override
    public Optional<SmartDeviceEntity> getById(long id) {
        return smartDeviceRepository.findById(id);
    }

    @Override
    public Collection<SmartDeviceEntity> getAllPropertyDevices(long propertyId) {
        return smartDeviceRepository.findAllByPropertyId(propertyId);
    }
}
