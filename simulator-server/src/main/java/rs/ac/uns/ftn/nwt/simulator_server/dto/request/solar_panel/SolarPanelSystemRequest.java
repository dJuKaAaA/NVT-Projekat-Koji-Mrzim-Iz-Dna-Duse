package rs.ac.uns.ftn.nwt.simulator_server.dto.request.solar_panel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@Getter
@Setter
@ToString
public class SolarPanelSystemRequest {
    private long id;
    private SolarPanelSystemCommand command;
    private Collection<SolarPanel> panels;
    private double latitude;
    private double longitude;
}
