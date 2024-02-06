package nvt.project.smart_home.main.feature.device.washing_machine.mapper;


import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineHistoryWebResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WashingMachineHistoryMapper {
    @Mapping( target = "timestamp",source = "timestamp")
    WashingMachineHistoryWebResponseDto entityToDto(WashingMachineAppointmentHistoryEntity entity);
}
