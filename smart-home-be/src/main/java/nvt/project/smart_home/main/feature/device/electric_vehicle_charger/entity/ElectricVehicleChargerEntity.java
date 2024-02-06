package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.exception.ChargersOccupiedException;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.exception.ChargingVehicleNotFoundException;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("ELECTRIC_VEHICLE_CHARGER")
public class ElectricVehicleChargerEntity extends SmartDeviceEntity {

    private double chargePower;

    private int chargerCount;

    private int chargersOccupied;

    private double chargeLimit;    // npr. kada se vozilo napuni do 90%, prekini punjenje

    @OneToMany(cascade = {CascadeType.ALL})
    private List<ChargingVehicleEntity> vehiclesCharging = new ArrayList<>();

    public void addVehicleForCharging(ChargingVehicleEntity chargingVehicle) {
        if (chargersOccupied >= chargerCount) {
            throw new ChargersOccupiedException();
        }

        vehiclesCharging.add(chargingVehicle);
        chargersOccupied++;
    }

    public void removeVehicle(Long chargingVehicleId) {
        boolean foundVehicle = false;
        for (ChargingVehicleEntity chargingVehicle : vehiclesCharging) {
            if (chargingVehicle.getId().equals(chargingVehicleId)) {
                vehiclesCharging.remove(chargingVehicle);
                this.chargersOccupied--;
                foundVehicle = true;
                break;
            }
        }

        if (!foundVehicle) {
            throw new ChargingVehicleNotFoundException("Could not find vehicle with id %s in being charged in this charger!".formatted(chargingVehicleId));
        }
    }

    public void addChargeToVehicle(Long chargingVehicleId, double power) {
        boolean foundVehicle = false;
        for (ChargingVehicleEntity chargingVehicle : vehiclesCharging) {
            if (chargingVehicle.getId().equals(chargingVehicleId)) {
                chargingVehicle.setCurrentPower(chargingVehicle.getCurrentPower() + power);
                foundVehicle = true;
                break;
            }
        }

        if (!foundVehicle) {
            throw new ChargingVehicleNotFoundException("Could not find vehicle with id %s in being charged in this charger!".formatted(chargingVehicleId));
        }

    }

}
