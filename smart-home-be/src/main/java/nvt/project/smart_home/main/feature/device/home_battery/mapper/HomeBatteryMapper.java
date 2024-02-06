package nvt.project.smart_home.main.feature.device.home_battery.mapper;

import nvt.project.smart_home.main.feature.device.home_battery.dto.request.HomeBatteryRequestDto;
import nvt.project.smart_home.main.feature.device.home_battery.dto.response.HomeBatteryResponseDto;
import nvt.project.smart_home.main.feature.device.home_battery.entity.HomeBatteryEntity;
import org.mapstruct.Mapper;

@Mapper
public interface HomeBatteryMapper {

    HomeBatteryResponseDto entityToResponseDto(HomeBatteryEntity entity);
    HomeBatteryEntity requestDtoToEntity(HomeBatteryRequestDto requestDto);

}
