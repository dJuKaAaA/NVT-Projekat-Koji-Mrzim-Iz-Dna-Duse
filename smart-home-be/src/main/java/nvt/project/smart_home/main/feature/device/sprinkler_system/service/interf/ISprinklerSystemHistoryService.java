package nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf;

import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SprinklerSystemHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response.SprinklerSystemHistoryResponseWebDto;

import java.util.List;

public interface ISprinklerSystemHistoryService {
    List<SprinklerSystemHistoryResponseWebDto> getHistoryOfActions(long deviceId, SprinklerSystemHistoryWebRequestDto historyResponseWebDto);
}
