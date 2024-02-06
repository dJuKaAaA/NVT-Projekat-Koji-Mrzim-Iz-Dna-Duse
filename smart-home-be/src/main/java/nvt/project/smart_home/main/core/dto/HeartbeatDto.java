package nvt.project.smart_home.main.core.dto;

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
