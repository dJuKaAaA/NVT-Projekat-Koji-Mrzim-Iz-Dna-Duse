package nvt.project.smart_home.main.feature.device.home_battery.service.interf;

import nvt.project.smart_home.main.feature.device.home_battery.dto.request.HomeBatteryRequestDto;
import nvt.project.smart_home.main.feature.device.home_battery.dto.response.HomeBatteryResponseDto;

public interface IHomeBatteryService {

    HomeBatteryResponseDto create(HomeBatteryRequestDto request);
    HomeBatteryResponseDto getById(Long id);
}
