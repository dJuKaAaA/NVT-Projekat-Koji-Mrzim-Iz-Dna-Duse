package nvt.project.smart_home.main.feature.device.ambient_sensor.entity;

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
@DiscriminatorValue("AMBIENT_SENSOR")
public class AmbientSensorEntity extends SmartDeviceEntity {

}
