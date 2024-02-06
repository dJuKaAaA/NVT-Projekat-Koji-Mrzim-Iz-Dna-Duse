package nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.command;

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
}
