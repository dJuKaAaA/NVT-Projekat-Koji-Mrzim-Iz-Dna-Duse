package rs.ac.uns.ftn.nwt.simulator_server.utils.ambient_sensor;

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