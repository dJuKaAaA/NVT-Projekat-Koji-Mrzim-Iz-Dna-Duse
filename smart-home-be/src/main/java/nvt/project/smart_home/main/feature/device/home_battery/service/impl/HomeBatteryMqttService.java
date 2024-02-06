package nvt.project.smart_home.main.feature.device.home_battery.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.ws_handler.HomeBatteryWebSocketHandler;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.feature.device.home_battery.constants.PowerConsumptionType;
import nvt.project.smart_home.main.feature.device.home_battery.mqtt_dto.HomeBatteryMqttDto;
import nvt.project.smart_home.main.feature.device.home_battery.service.interf.IHomeBatteryMqttService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.HOME_BATTERY_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.HOME_BATTERY_FIELD_POWER_CONSUMPTION;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.*;

@RequiredArgsConstructor
@Service
public class HomeBatteryMqttService implements IHomeBatteryMqttService {

    private final InfluxDBQueryService influxDBQueryService;
    private final HomeBatteryWebSocketHandler homeBatteryWebSocketHandler;
    private final ObjectMapper objectMapper;


    @SneakyThrows
    @Override
    public void sendWsMessage(HomeBatteryMqttDto mqttResponse) {
        String message = objectMapper.writeValueAsString(mqttResponse);
        homeBatteryWebSocketHandler.broadcastMessage(mqttResponse.getPropertyId(), message);
    }

    @Override
    public Collection<FluxResultDto<Double>> getConsumedEnergy(Long propertyId, int minutesInPast, String powerConsumptionTypeString) {
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));
        PowerConsumptionType powerConsumptionType;
        try {
            powerConsumptionType = Enum.valueOf(PowerConsumptionType.class, powerConsumptionTypeString);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid power consumption type!");
        }
        switch (powerConsumptionType) {
            case PowerConsumptionType.CONSUMED -> tags.put(HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE, HOME_BATTERY_TAG_VALUE_CONSUMED_CONSUMED);
            case PowerConsumptionType.TAKEN_FROM_NETWORK -> tags.put(HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE, TAG_VALUE_CONSUMED_TAKEN_FROM_NETWORK);
        }
        return influxDBQueryService.getWithTimeStamp(
                minutesInPast,
                HOME_BATTERY_DEVICE_NAME,
                HOME_BATTERY_FIELD_POWER_CONSUMPTION,
                tags);
    }

    @Override
    public Collection<FluxResultDto<Double>> getConsumedEnergy(Long propertyId, LocalDateTime startDate, LocalDateTime endDate, String powerConsumptionTypeString) {
        Map<String, String> tags = new HashMap<>();
        PowerConsumptionType powerConsumptionType;
        try {
            powerConsumptionType = Enum.valueOf(PowerConsumptionType.class, powerConsumptionTypeString);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid power consumption type!");
        }
        switch (powerConsumptionType) {
            case PowerConsumptionType.CONSUMED -> tags.put(HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE, HOME_BATTERY_TAG_VALUE_CONSUMED_CONSUMED);
            case PowerConsumptionType.TAKEN_FROM_NETWORK -> tags.put(HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE, TAG_VALUE_CONSUMED_TAKEN_FROM_NETWORK);
        }
        return influxDBQueryService.getWithTimeStamp(
                startDate,
                endDate,
                HOME_BATTERY_DEVICE_NAME,
                HOME_BATTERY_FIELD_POWER_CONSUMPTION,
                tags);
    }

}
