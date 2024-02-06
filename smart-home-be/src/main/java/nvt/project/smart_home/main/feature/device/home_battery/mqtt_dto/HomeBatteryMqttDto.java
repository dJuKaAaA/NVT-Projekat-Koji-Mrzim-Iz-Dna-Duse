package nvt.project.smart_home.main.feature.device.home_battery.mqtt_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nvt.project.smart_home.main.feature.device.home_battery.constants.PowerConsumptionType;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeBatteryMqttDto {
    private long propertyId;
    private double powerConsumed;
    private Instant timestamp;
    private PowerConsumptionType powerConsumptionType;
}
