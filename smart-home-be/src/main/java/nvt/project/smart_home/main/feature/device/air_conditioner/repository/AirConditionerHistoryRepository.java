package nvt.project.smart_home.main.feature.device.air_conditioner.repository;

import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerAppointmentHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirConditionerHistoryRepository extends JpaRepository<AirConditionerAppointmentHistoryEntity, Long> {

    Page<AirConditionerAppointmentHistoryEntity> findAllByDeviceId(long deviceId, Pageable pageable);
}
