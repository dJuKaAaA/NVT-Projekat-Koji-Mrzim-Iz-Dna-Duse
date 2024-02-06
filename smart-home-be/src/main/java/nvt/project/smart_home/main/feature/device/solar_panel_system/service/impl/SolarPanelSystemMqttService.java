package nvt.project.smart_home.main.feature.device.solar_panel_system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.ws_handler.SolarPanelSystemWebSocketHandler;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.exception.UserNotFoundException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.core.repository.UserRepository;
import nvt.project.smart_home.main.feature.device.solar_panel_system.entity.SolarPanelSystemEntity;
import nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.response.SolarPanelSystemMqttResponse;
import nvt.project.smart_home.main.feature.device.solar_panel_system.repository.SolarPanelSystemRepository;
import nvt.project.smart_home.main.feature.device.solar_panel_system.service.interf.ISolarPanelSystemMqttService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.SOLAR_PANEL_SYSTEM_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.SOLAR_PANEL_SYSTEM_FIELD_ACTION;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.SOLAR_PANEL_SYSTEM_FIELD_ENERGY;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TAG_KEY_USER_ID;

@RequiredArgsConstructor
@Service
public class SolarPanelSystemMqttService implements ISolarPanelSystemMqttService {

    private final SolarPanelSystemWebSocketHandler solarPanelSystemWebSocketHandler;
    private final ObjectMapper objectMapper;
    private final InfluxDBQueryService influxDBQueryService;
    private final SolarPanelSystemRepository solarPanelSystemRepository;
    private final UserRepository userRepository;

    @SneakyThrows
    @Override
    public void sendWsMessage(SolarPanelSystemMqttResponse mqttResponse) {
        String message = objectMapper.writeValueAsString(mqttResponse);
        solarPanelSystemWebSocketHandler.broadcastMessage(mqttResponse.getId(), message);
    }

    @Override
    public Collection<FluxResultDto<Integer>> getActions(long id, String userEmail, LocalDateTime startDate, LocalDateTime endDate) {
        SolarPanelSystemEntity solarPanelSystem = solarPanelSystemRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Solar panel system not found!"));
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);
        // TODO: Check if that user is one of the owners of that solar panel system

        Map<String, String> tags = new HashMap<>();
        tags.put("id", String.valueOf(id));
        tags.put(TAG_KEY_USER_ID, String.valueOf(user.getId()));
        return influxDBQueryService.getWithTimeStamp(
                startDate,
                endDate,
                SOLAR_PANEL_SYSTEM_DEVICE_NAME,
                SOLAR_PANEL_SYSTEM_FIELD_ACTION,
                tags);
    }

    @Override
    public Collection<FluxResultDto<Double>> getEnergyProduced(long id, int minutesInPast) {
        SolarPanelSystemEntity solarPanelSystem = solarPanelSystemRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Solar panel system not found!"));
        // TODO: Check if that user is one of the owners of that solar panel system

        Map<String, String> tags = new HashMap<>();
        return influxDBQueryService.getWithTimeStamp(
                minutesInPast,
                SOLAR_PANEL_SYSTEM_DEVICE_NAME,
                SOLAR_PANEL_SYSTEM_FIELD_ENERGY,
                tags);
    }
}
