package rs.ac.uns.ftn.nwt.simulator_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeartbeatDto {
    private long deviceId;
    private boolean failed;
}
