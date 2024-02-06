package nvt.project.smart_home.main.feature.device.lamp.service.interf;

import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampWebResponseDto;

public interface ILampService {
    LampWebResponseDto create(LampWebRequestDto request);
    LampWebResponseDto getById(Long id);
    LampWebResponseDto setBulb(Long id, boolean bulbOn, String triggeredBy);
    LampWebResponseDto setAuto(Long id, boolean autoOn, String triggeredBy);
}
