package nvt.project.smart_home.main.feature.device.washing_machine.repository;

import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineWorkAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WashingMachineSchedulingRepository extends JpaRepository<WashingMachineWorkAppointmentEntity, Long> {
}
