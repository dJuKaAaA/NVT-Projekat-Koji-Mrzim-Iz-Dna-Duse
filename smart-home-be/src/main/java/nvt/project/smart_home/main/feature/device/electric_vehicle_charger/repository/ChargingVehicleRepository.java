package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.repository;

import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ChargingVehicleEntity;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ElectricVehicleChargerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ChargingVehicleRepository extends JpaRepository<ChargingVehicleEntity, Long> {
    Collection<ChargingVehicleEntity> findByElectricVehicleCharger(ElectricVehicleChargerEntity electricVehicleCharger);
}
