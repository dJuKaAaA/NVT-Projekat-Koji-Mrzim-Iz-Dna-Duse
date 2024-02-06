package nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCurrentWorkMode;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashingMachineMqttResponse {
    private long id;
    private Long appointmentId;
    private WashingMachineCurrentWorkMode workMode;
    private Instant timestamp;
}
