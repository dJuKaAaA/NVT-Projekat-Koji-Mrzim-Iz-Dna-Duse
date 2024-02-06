package nvt.project.smart_home.main.core.influxdb.fluxResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DoubleFluxResult {
    private double value;
    private Instant timestamp;


}
