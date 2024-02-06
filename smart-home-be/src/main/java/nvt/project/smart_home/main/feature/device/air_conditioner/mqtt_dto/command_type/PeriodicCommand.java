package nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.command_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicCommand {
    private Long appointmentId;
    String startTime;
    String endTime;
    double wantedTemperature;
}
