package rs.ac.uns.ftn.nwt.simulator_server.dto.request.washing_machine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.nwt.simulator_server.constants.washing_machine.WashingMachineCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.washing_machine.command_type.PeriodicCommand;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WashingMachineRequest {
    private long id;
    private WashingMachineCommand command;

    // one of this will be null base on command
    private PeriodicCommand periodicCommand;


}


