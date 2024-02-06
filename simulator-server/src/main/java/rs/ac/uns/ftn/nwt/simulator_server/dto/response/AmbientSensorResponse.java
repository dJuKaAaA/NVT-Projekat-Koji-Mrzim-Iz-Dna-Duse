package rs.ac.uns.ftn.nwt.simulator_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmbientSensorResponse {

    private long id;
    private double temperature;
    private double humidity;
    private Date timestamp;
}
