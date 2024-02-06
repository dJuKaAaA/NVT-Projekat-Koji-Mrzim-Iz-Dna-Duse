package rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.state;

import lombok.*;
import rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.request.SprinklerSystemScheduleRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinklerSystemState {
    private long id;
    private boolean systemOn;
    private List<SprinklerSystemScheduleRequest> schedule;
}
