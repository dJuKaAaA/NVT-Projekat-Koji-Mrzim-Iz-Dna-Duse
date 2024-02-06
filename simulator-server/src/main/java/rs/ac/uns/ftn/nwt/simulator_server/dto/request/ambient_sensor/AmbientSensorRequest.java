package rs.ac.uns.ftn.nwt.simulator_server.dto.request.ambient_sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import rs.ac.uns.ftn.nwt.simulator_server.constants.AmbientSensorCommand;

@Getter
@Setter
@ToString
public class AmbientSensorRequest {
    private long id;
    private AmbientSensorCommand command;
}

