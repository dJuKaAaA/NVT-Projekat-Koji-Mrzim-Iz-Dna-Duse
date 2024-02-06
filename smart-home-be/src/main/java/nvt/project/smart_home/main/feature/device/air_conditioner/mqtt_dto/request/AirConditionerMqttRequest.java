package nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.request;

import lombok.*;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCommand;
import nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.command_type.NormalCommand;
import nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.command_type.PeriodicCommand;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirConditionerMqttRequest {

    private long id;
    private double minTemperature;
    private double maxTemperature;
    private AirConditionerCommand command;

    // one of this will be null base on command or both if is off command
    private NormalCommand normalCommand;
    private PeriodicCommand periodicCommand;


}


