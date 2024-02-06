package nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf;

import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SetScheduleRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SetSystemOnOffRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SprinklerSystemRequestWebDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response.SprinklerSystemResponseWebDto;

public interface ISprinklerSystemService {

    SprinklerSystemResponseWebDto create(SprinklerSystemRequestWebDto request);
    SprinklerSystemResponseWebDto getById(Long id);
    SprinklerSystemResponseWebDto setSystemOn(Long id, SetSystemOnOffRequestDto systemOnOffRequestDto);
    SprinklerSystemResponseWebDto setSchedule(SetScheduleRequestDto sprinklerSystemRequest);

}
