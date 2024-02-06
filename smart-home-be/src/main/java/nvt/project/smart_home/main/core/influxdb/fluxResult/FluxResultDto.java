package nvt.project.smart_home.main.core.influxdb.fluxResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FluxResultDto<T> {
    private T value;
    private Instant timestamp;
}
