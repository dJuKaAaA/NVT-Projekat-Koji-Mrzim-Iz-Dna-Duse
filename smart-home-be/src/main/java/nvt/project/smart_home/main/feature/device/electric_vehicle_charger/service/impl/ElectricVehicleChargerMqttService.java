package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.ws_handler.ElectricVehicleChargerWebSocketHandler;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultWithTagsDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.response.ElectricVehicleChargerMqttResponse;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.interf.IElectricVehicleChargerMqttService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.ELECTRIC_VEHICLE_CHARGER_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.ELECTRIC_VEHICLE_CHARGER_FIELD_ACTION;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TAG_KEY_USER_ID;

@RequiredArgsConstructor
@Service
public class ElectricVehicleChargerMqttService implements IElectricVehicleChargerMqttService {

    private final ElectricVehicleChargerWebSocketHandler electricVehicleChargerWebSocketHandler;
    private final ObjectMapper objectMapper;
    private final InfluxDBQueryService influxDBQueryService;

    @SneakyThrows
    @Override
    public void sendWsMessage(ElectricVehicleChargerMqttResponse mqttResponse) {
        String message = objectMapper.writeValueAsString(mqttResponse);
        electricVehicleChargerWebSocketHandler
                .broadcastMessage(mqttResponse.getId() + "_" + mqttResponse.getChargingVehicle().getId(), message);
    }

    @Override
    public Collection<FluxResultWithTagsDto<Integer>> getAllActions(long deviceId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, String> tags = new HashMap<>();
        tags.put("id", String.valueOf(deviceId));
        return influxDBQueryService.getWithTimeStampWithTags(
                startDate,
                endDate,
                ELECTRIC_VEHICLE_CHARGER_DEVICE_NAME,
                ELECTRIC_VEHICLE_CHARGER_FIELD_ACTION,
                tags);
    }

    @Override
    public Collection<FluxResultWithTagsDto<Integer>> getActionsByUser(long deviceId, long userId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_USER_ID, String.valueOf(userId));
        tags.put("id", String.valueOf(deviceId));
        return influxDBQueryService.getWithTimeStampWithTags(
                startDate,
                endDate,
                ELECTRIC_VEHICLE_CHARGER_DEVICE_NAME,
                ELECTRIC_VEHICLE_CHARGER_FIELD_ACTION,
                tags);
    }

}
