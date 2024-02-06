package rs.ac.uns.ftn.nwt.simulator_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.nwt.simulator_server.constants.air_conditioner.AirConditionerCurrentWorkMode;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirConditionerResponse {

    private long id;
    private Long appointmentId;
    private AirConditionerCurrentWorkMode workMode;
    private Double temperature;
    private Date timestamp;
}
