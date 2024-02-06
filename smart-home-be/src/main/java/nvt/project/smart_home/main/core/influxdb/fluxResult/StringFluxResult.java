package nvt.project.smart_home.main.core.influxdb.fluxResult;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StringFluxResult {
    private String value;
    private Instant timestamp;

}
