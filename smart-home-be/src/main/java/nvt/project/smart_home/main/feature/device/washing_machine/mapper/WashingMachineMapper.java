package nvt.project.smart_home.main.feature.device.washing_machine.mapper;

import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineWebResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = WashingMachineAppointmentMapper.class)
public interface WashingMachineMapper {

    WashingMachineEntity dtoToEntity(WashingMachineWebRequestDto requestDto);

    @Mapping(source = "workMode", target = "workMode")
    @Mapping(source = "workPlan", target = "workPlan")
    WashingMachineWebResponseDto entityToDto(WashingMachineEntity entity);

}
