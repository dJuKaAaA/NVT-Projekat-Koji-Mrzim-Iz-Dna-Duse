package nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCurrentWorkMode;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirConditionerMqttResponse {
    private long id;
    private Long appointmentId;
    private AirConditionerCurrentWorkMode workMode;
    private Double temperature;
    private Instant timestamp;
}
