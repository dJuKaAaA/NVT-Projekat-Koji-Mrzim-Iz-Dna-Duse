package rs.ac.uns.ftn.nwt.simulator_server.dto.request.air_condtioner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.nwt.simulator_server.constants.air_conditioner.AirConditionerCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.air_condtioner.command_type.NormalCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.air_condtioner.command_type.PeriodicCommand;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirConditionerRequest {

    private long id;
    private double minTemperature;
    private double maxTemperature;
    private AirConditionerCommand command;

    // one of this will be null base on command or both if is off command
    private NormalCommand normalCommand;
    private PeriodicCommand periodicCommand;


}


