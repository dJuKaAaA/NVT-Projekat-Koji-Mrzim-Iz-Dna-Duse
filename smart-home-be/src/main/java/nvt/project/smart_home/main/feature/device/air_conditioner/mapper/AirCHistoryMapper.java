package nvt.project.smart_home.main.feature.device.air_conditioner.mapper;

import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCHistoryResponseWebDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AirCHistoryMapper {

    @Mapping( target = "timestamp",source = "timestamp")
    AirCHistoryResponseWebDto entityToDto(AirConditionerAppointmentHistoryEntity entity);
}
