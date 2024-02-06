package nvt.project.smart_home.main.feature.device.ambient_sensor.mqtt_dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmbientSensorMqttResponse {
    private long id;
    private double temperature;
    private double humidity;
    private Instant timestamp;
}
