package rs.ac.uns.ftn.nwt.simulator_server.dto.response;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolarPanelSystemResponse {
    private long id;
    private double energy;  // kW
    private Date timestamp;
}
