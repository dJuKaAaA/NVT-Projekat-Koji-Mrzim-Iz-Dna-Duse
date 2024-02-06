package nvt.project.smart_home.main.core.influxdb.fluxResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BooleanFluxResult {
    private double value;
    private Instant timestamp;
}
