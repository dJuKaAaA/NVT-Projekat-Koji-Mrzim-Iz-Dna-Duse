package nvt.project.smart_home.main.feature.device.lamp.entity;

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
@DiscriminatorValue("LAMP")
public class LampEntity extends SmartDeviceEntity {
    private double lightLevel;
    private boolean autoModeOn;
    private boolean bulbOn;
}
