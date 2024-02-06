package nvt.project.smart_home.test_script.ambient_sensor_utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AmbientSensorDataEntry {

    private LocalDateTime dateTime;
    private double temperature;
    private double humidity;
}