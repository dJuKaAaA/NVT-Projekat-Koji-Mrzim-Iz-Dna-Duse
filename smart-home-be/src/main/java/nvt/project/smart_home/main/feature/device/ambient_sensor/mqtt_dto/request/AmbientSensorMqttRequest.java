package nvt.project.smart_home.main.feature.device.ambient_sensor.mqtt_dto.request;

import lombok.*;
import nvt.project.smart_home.main.feature.device.ambient_sensor.constants.AmbientSensorCommand;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmbientSensorMqttRequest {
    private long id;
    private AmbientSensorCommand command;
}

