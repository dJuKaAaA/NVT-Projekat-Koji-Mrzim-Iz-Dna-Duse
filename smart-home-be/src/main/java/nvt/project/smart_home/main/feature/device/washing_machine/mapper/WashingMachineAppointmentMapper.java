package nvt.project.smart_home.main.feature.device.washing_machine.mapper;

import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineWorkAppointmentEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineWorkAppointmentWebResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WashingMachineAppointmentMapper {

    WashingMachineWorkAppointmentEntity dtoToEntity(WashingMachineWorkAppointmentWebRequestDto requestDto);

    @Mapping(source = "bookedByUser.email", target = "executor")
    WashingMachineWorkAppointmentWebResponseDto entityToDto(WashingMachineWorkAppointmentEntity requestDto);
}
