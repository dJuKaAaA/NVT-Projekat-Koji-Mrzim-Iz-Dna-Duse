package nvt.project.smart_home.main.core.service.interf;

import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface ISmartDeviceService {
    SmartDeviceResponseDto getById(Long id) throws IOException;
    Collection<SmartDeviceResponseDto> getByPropertyId(Long propertyId);

    Optional<SmartDeviceEntity> getById(long id);

    Collection<SmartDeviceEntity> getAllPropertyDevices(long propertyId);
}
