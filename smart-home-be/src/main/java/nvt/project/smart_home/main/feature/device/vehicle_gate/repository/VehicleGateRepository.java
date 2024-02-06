package nvt.project.smart_home.main.feature.device.vehicle_gate.repository;

import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleGateRepository extends JpaRepository<VehicleGateEntity, Long> {
}
