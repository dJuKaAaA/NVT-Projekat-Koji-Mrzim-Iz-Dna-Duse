package rs.ac.uns.ftn.nwt.simulator_server.dto.request.air_condtioner.command_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NormalCommand {
    double wantedTemperature;
}
