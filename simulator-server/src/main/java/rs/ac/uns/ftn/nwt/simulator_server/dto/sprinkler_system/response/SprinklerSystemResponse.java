package rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinklerSystemResponse {
    private long id;
    private boolean systemOn;
    private String triggeredBy;
    private Date timestamp;
}
