package rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.state;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LampState {
    private Boolean bulbOn;
    private Boolean autoModeOn;
}
