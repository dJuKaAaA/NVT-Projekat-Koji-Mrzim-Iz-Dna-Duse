package rs.ac.uns.ftn.nwt.simulator_server.dto.request.solar_panel;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolarPanel {
    private double area;
    private double efficiency;
}
