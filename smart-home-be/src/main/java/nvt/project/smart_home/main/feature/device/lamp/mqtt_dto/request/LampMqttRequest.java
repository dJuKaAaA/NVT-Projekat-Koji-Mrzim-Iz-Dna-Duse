package nvt.project.smart_home.main.feature.device.lamp.mqtt_dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LampMqttRequest {
    private long id;
    private Boolean bulbOn;
    private Boolean autoModeOn;
    private String triggeredBy;
}
