package nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirCHistoryResponseWebDto {

    String executor;
    private String action;
    private String timestamp;
    private Double temperature;
}
