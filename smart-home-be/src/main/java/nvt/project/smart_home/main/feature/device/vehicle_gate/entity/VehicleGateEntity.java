package nvt.project.smart_home.main.feature.device.vehicle_gate.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.feature.device.vehicle_gate.constants.VehicleGateSystemCommand;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("VEHICLE_GATE")
public class VehicleGateEntity extends SmartDeviceEntity {
    private boolean isOpen;
    private boolean isAlwaysOpen;
    private boolean isPrivateMode;
    private String lastLicencePlateIn;
    private Date lastInDate;
    private String lastLicencePlateOut;
    private Date lastOutDate;
    private VehicleGateSystemCommand lastInCommand;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> allowedLicencePlates;
}
