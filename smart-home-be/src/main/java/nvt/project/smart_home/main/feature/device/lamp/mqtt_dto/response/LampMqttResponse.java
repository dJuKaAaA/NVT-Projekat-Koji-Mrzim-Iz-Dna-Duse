package nvt.project.smart_home.main.feature.device.lamp.mqtt_dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampCommand;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LampMqttResponse {
    private Long id;
    private Double lightLevel;
    private Boolean bulbOn;
    private Boolean autoModeOn;
    private Date timestamp;
    private LampCommand command;
    private String triggeredBy;
}
