package nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.reqeust;

import lombok.*;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCommand;
import nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.command.PeriodicCommand;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashingMachineMqttRequest {
    private long id;
    private WashingMachineCommand command;

    // one of this will be null base on command
    private PeriodicCommand periodicCommand;


}


