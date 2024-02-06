package nvt.project.smart_home.main.feature.device.air_conditioner.repository;

import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerWorkAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirConditionerSchedulingRepository extends JpaRepository<AirConditionerWorkAppointmentEntity, Long> {
}
