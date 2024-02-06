package nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinklerSystemMqttRequestDto {
    private long id;
    private boolean systemOn;
    private String userEmail;
    private List<SprinklerSystemScheduleMqttRequestDto> schedule;
}
