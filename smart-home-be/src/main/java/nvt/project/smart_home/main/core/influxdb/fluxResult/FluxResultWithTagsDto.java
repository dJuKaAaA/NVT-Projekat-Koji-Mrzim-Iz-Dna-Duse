package nvt.project.smart_home.main.core.influxdb.fluxResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FluxResultWithTagsDto<T> {
    private T value;
    private Map<String,String> tags;
    private Instant timestamp;
}
