package nvt.project.smart_home.main.feature.device.air_conditioner.mapper;

import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCWebResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = AirConditionerAppointmentMapper.class)
public interface AirConditionerMapper {



    @Mapping(source = "workMode", target = "workMode")
    @Mapping(source = "workPlan", target = "workPlan")
    AirCWebResponseDto entityToDto(AirConditionerEntity entity);

    AirConditionerEntity dtoToEntity(AirCWebRequestDto requestDto);

}
