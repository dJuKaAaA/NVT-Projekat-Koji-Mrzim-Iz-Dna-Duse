package rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SprinklerSystemRequest {
    private long id;
    private boolean systemOn;
    private String userEmail;
    private List<SprinklerSystemScheduleRequest> schedule;
}
