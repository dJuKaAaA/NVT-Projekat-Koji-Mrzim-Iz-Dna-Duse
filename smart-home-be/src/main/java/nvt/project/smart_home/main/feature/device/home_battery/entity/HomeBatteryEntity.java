package nvt.project.smart_home.main.feature.device.home_battery.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("HOME_BATTERY")
public class HomeBatteryEntity extends SmartDeviceEntity {
    private double current;
    private double capacity;
}
