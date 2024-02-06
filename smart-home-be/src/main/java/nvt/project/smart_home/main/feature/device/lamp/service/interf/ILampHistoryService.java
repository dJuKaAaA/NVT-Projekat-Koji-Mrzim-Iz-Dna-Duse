package nvt.project.smart_home.main.feature.device.lamp.service.interf;

import nvt.project.smart_home.main.feature.device.lamp.constants.LampValueType;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampActionHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampCommandHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampValueHistoryWebResponseDto;

import java.util.List;

public interface ILampHistoryService {
    List<LampValueHistoryWebResponseDto> getValuesHistory(long deviceId, LampHistoryWebRequestDto request, LampValueType valueType);
    List<LampCommandHistoryWebResponseDto> getCommandHistory(long deviceId, LampActionHistoryWebRequestDto request);

}
