package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
public class ChargingVehicleEntity {

    @Id
    @GeneratedValue
    private Long id;

    private double currentPower;
    private double maxPower;

    @ManyToOne
    private ElectricVehicleChargerEntity electricVehicleCharger;

}
