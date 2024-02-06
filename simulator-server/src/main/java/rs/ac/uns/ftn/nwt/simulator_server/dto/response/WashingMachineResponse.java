package rs.ac.uns.ftn.nwt.simulator_server.dto.response;

import lombok.*;
import rs.ac.uns.ftn.nwt.simulator_server.constants.air_conditioner.AirConditionerCurrentWorkMode;
import rs.ac.uns.ftn.nwt.simulator_server.constants.washing_machine.WashingMachineCurrentWorkMode;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashingMachineResponse {
    private long id;
    private Long appointmentId;
    private WashingMachineCurrentWorkMode workMode;
    private Date timestamp;
}
