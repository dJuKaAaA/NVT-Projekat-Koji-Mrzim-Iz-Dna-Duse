package nvt.project.smart_home.main.feature.device.washing_machine.repository;

import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineAppointmentHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WashingMachineHistoryRepository extends JpaRepository<WashingMachineAppointmentHistoryEntity, Long> {
    Page<WashingMachineAppointmentHistoryEntity> findAllByDeviceId(long deviceId, Pageable pageable);
}
