package nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AmbientSensorHistoryWebResponseDto {
    private String timestamp;
    private double temperature;
    private double humidity;
}
