package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.repository;

import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ElectricVehicleChargerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricVehicleChargerRepository extends JpaRepository<ElectricVehicleChargerEntity, Long> {
}
