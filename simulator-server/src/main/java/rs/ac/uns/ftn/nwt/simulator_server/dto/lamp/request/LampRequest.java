package rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LampRequest {
    private long id;
    private Boolean bulbOn;
    private Boolean autoModeOn;
    private String triggeredBy;
}
