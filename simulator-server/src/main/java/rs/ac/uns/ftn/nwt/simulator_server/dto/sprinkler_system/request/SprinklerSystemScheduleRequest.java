package rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.request;

import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinklerSystemScheduleRequest {
    private String startTime;
    private String endTime;
    private List<DayOfWeek> days;
}
