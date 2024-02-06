package nvt.project.smart_home.main.feature.device.air_conditioner.mapper;

import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerWorkAppointmentEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCWorkAppointmentWebResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AirConditionerAppointmentMapper {

    AirConditionerWorkAppointmentEntity dtoToEntity(AirCWorkAppointmentWebRequestDto requestDto);


    @Mapping(source = "bookedByUser.email", target = "executor")
    AirCWorkAppointmentWebResponseDto entityToDto(AirConditionerWorkAppointmentEntity requestDto);

}
