package nvt.project.smart_home.main.feature.device.air_conditioner.service.interf;

import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerAppointmentHistoryEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAirConditionerHistoryService {
    void save(AirConditionerAppointmentHistoryEntity history);

    List<AirConditionerAppointmentHistoryEntity> findByDeviceId(long deviceId, Pageable pageable);
}
