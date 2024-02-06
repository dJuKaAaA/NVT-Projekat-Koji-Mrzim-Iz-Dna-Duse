package rs.ac.uns.ftn.nwt.simulator_server.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprinklerSystemResponse {
    private Long id;
    private boolean isActive;
}
