package rs.ac.uns.ftn.nwt.simulator_server.dto.request.washing_machine.command_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicCommand {

    private Long appointmentId;
    String startTime;
}
