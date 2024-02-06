package nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinklerSystemMqttResponse {
    private long id;
    private boolean systemOn;
    private String triggeredBy;
    private Date timestamp;
}
