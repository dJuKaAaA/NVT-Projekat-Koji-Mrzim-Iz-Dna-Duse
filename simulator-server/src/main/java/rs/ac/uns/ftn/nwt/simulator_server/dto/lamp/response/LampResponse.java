package rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.response;

import lombok.*;
import rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.constants.LampCommand;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LampResponse {
    private Long id;
    private Double lightLevel;
    private Boolean bulbOn;
    private Boolean autoModeOn;
    private Date timestamp;
    private LampCommand command;
    private String triggeredBy;
}
