package nvt.project.smart_home.main.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.response.ElectricVehicleChargerMqttResponse;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.interf.IElectricVehicleChargerMqttService;
import nvt.project.smart_home.main.core.dto.HeartbeatDto;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.response.AirConditionerMqttResponse;
import nvt.project.smart_home.main.feature.device.air_conditioner.service.interf.IAirConditionerMqttService;
import nvt.project.smart_home.main.feature.device.ambient_sensor.mqtt_dto.response.AmbientSensorMqttResponse;
import nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf.IAmbientSensorMqttService;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampMode;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampValueType;
import nvt.project.smart_home.main.feature.device.lamp.entity.LampEntity;
import nvt.project.smart_home.main.feature.device.lamp.mqtt_dto.response.LampMqttResponse;
import nvt.project.smart_home.main.feature.device.lamp.repository.LampRepository;
import nvt.project.smart_home.main.feature.device.lamp.service.interf.ILampMqttService;
import nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.response.SolarPanelSystemMqttResponse;
import nvt.project.smart_home.main.feature.device.solar_panel_system.service.interf.ISolarPanelSystemMqttService;
import nvt.project.smart_home.main.feature.device.sprinkler_system.SprinklerSystemStatus;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.response.SprinklerSystemMqttResponse;
import nvt.project.smart_home.main.feature.device.sprinkler_system.repository.SprinklerSystemRepository;
import nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf.ISprinklerSystemMqttService;
import nvt.project.smart_home.main.feature.device.vehicle_gate.constants.VehicleGateMode;
import nvt.project.smart_home.main.feature.device.vehicle_gate.constants.VehicleGateSystemCommand;
import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;
import nvt.project.smart_home.main.feature.device.vehicle_gate.mqtt_dto.response.VehicleGateMqttResponse;
import nvt.project.smart_home.main.feature.device.vehicle_gate.repository.VehicleGateRepository;
import nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf.IVehicleGateMqttService;
import nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.response.WashingMachineMqttResponse;
import nvt.project.smart_home.main.feature.device.washing_machine.service.impl.WashingMachineMqttService;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.time.Instant;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.*;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.*;

@RequiredArgsConstructor
@Component
public class MessageCallback implements MqttCallback {

    private final ObjectMapper objectMapper;
    private final InfluxDBQueryService influxDBQueryService;
    private final IAmbientSensorMqttService ambientSensorMqttService;
    private final ISolarPanelSystemMqttService solarPanelSystemMqttService;
    private final ILampMqttService lampMqttService;
    private final IAirConditionerMqttService airConditionerMqttService;
    private final IVehicleGateMqttService vehicleGateMqttService;
    private final LampRepository lampRepository;
    private final VehicleGateRepository vehicleGateRepository;
    private final ISprinklerSystemMqttService sprinklerSystemMqttService;
    private final SprinklerSystemRepository sprinklerSystemRepository;
    private final IElectricVehicleChargerMqttService electricVehicleChargerMqttService;
    private final WashingMachineMqttService washingMachineMqttService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCallback.class);
    // TODO: Add services
    // ...

    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        LOGGER.warn("disconnected: {}", mqttDisconnectResponse.getReasonString());
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {
        LOGGER.error("error: {}", e.getMessage());
    }


    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws JsonProcessingException {
        String message = new String(mqttMessage.getPayload());

        if (topic.contains(RECEIVE_AMBIENT_SENSOR_TOPIC)) {
            handleAmbientSensorMessage(message);
        } else if (topic.contains(RECEIVE_SOLAR_PANEL_SYSTEM_TOPIC)) {
            SolarPanelSystemMqttResponse response = objectMapper.readValue(message, SolarPanelSystemMqttResponse.class);
            influxDBQueryService.save(response.getId(), SOLAR_PANEL_SYSTEM_DEVICE_NAME, SOLAR_PANEL_SYSTEM_FIELD_ENERGY, response.getEnergy(), response.getTimestamp().toInstant());
            solarPanelSystemMqttService.sendWsMessage(response);
        } else if (topic.contains(RECEIVE_AIR_CONDITIONER_TOPIC)) {
            handleAirConditionerMessage(message);
        } else if (topic.contains(RECEIVE_LAMP_TOPIC)) handleLampMessage(message);
        else if (topic.contains(RECEIVE_VEHICLE_GATE_TOPIC)) handleVehicleGateMessage(message);
        else if (topic.contains(RECEIVE_SPRINKLER_SYSTEM_TOPIC)) handleSprinklerSystemMessage(message);
        else if (topic.contains(RECEIVE_ELECTRIC_VEHICLE_CHARGER_TOPIC)) {
            handleElectricVehicleChargerMessage(message);
        }
        else if (topic.contains(RECEIVE_WASHING_MACHINE_TOPIC)) handleWashingMachineMessage(message);
        else if (topic.contains(I_AM_ALIVE_TOPIC)) {
            HeartbeatDto heartbeat = objectMapper.readValue(message, HeartbeatDto.class);
            influxDBQueryService.save(heartbeat.getDeviceId(), HEARTBEAT_DEVICE_NAME, HEARTBEAT_FIELD, 0.0, Instant.now());
        }
    }

    private void handleWashingMachineMessage(String message) throws JsonProcessingException {
        WashingMachineMqttResponse response = objectMapper.readValue(message, WashingMachineMqttResponse.class);
        washingMachineMqttService.handleMqttResponse(response);
    }

    private void handleAirConditionerMessage(String message) throws JsonProcessingException {
        AirConditionerMqttResponse response = objectMapper.readValue(message, AirConditionerMqttResponse.class);
        influxDBQueryService.save(response.getId(), AIR_CONDITIONER_DEVICE_NAME, AIR_CONDITIONER_FIELD_TEMPERATURE, response.getTemperature(), response.getTimestamp());
        airConditionerMqttService.handleMqttResponse(response);
    }

    private void handleAmbientSensorMessage(String message) throws JsonProcessingException {
        AmbientSensorMqttResponse response = objectMapper.readValue(message, AmbientSensorMqttResponse.class);
        influxDBQueryService.save(response.getId(), AMBIENT_SENSOR_DEVICE_NAME, AMBIENT_SENSOR_FIELD_TEMPERATURE, response.getTemperature(), response.getTimestamp());
        influxDBQueryService.save(response.getId(), AMBIENT_SENSOR_DEVICE_NAME, AMBIENT_SENSOR_FIELD_HUMIDITY, response.getHumidity(), response.getTimestamp());
        ambientSensorMqttService.sendWsMessage(response);
    }

    private void handleLampMessage(String message) throws JsonProcessingException {
        LampMqttResponse response = objectMapper.readValue(message, LampMqttResponse.class);

        // database
        LampEntity lamp = lampRepository.findById(response.getId()).orElseThrow(() -> new SmartDeviceNotFoundException("Lamp not found!"));
        lamp.setLightLevel(response.getLightLevel());
        lamp.setBulbOn(response.getBulbOn());
        lamp.setAutoModeOn(response.getAutoModeOn());
        lampRepository.save(lamp);

        // influx
        HashMap<String, String> tags = new HashMap<>();

        // koristiti akcije umjesto upisa on off bulba
//        tags.put(LAMP_VALUE_TAG, LampValueType.BULB_ON.toString());
//        influxDBQueryService.save(response.getId(), LAMP_DEVICE_NAME, LAMP_VALUES_FIELD, response.getBulbOn() ? 1.0 : 0.0, response.getTimestamp().toInstant(), tags);
//        tags.clear();

        tags.put(LAMP_VALUE_TAG, LampValueType.ILLUMINATION.toString());
        influxDBQueryService.save(response.getId(), LAMP_DEVICE_NAME, LAMP_VALUES_FIELD, response.getLightLevel(), response.getTimestamp().toInstant(), tags);

        if (response.getCommand() != null && response.getTriggeredBy() != null) {
            tags.clear();
            tags.put(TRIGGERED_BY_TAG, response.getTriggeredBy());
            LampMode mode = LampMode.MANUAL_MODE;
            if (response.getAutoModeOn()) mode = LampMode.AUTO_MODE;
            tags.put(MODE_TAG, mode.toString());
            influxDBQueryService.save(response.getId(), LAMP_DEVICE_NAME, LAMP_ACTION_FIELD, response.getCommand().toString(), response.getTimestamp().toInstant(), tags);
        }

        // send web socket
        lampMqttService.sendWsMessage(response);
    }

    private void handleVehicleGateMessage(String message) throws JsonProcessingException {
        VehicleGateMqttResponse response = objectMapper.readValue(message, VehicleGateMqttResponse.class);

        // database
        VehicleGateEntity vehicleGate = vehicleGateRepository.findById(response.getId()).orElseThrow(() -> new SmartDeviceNotFoundException("Vehicle gate not found!"));
        vehicleGate.setOpen(response.isOpen());
        vehicleGate.setAlwaysOpen(response.isAlwaysOpen());
        vehicleGate.setPrivateMode(response.isPrivateMode());
        if (response.getCommand() != null) vehicleGate.setLastInCommand(response.getCommand());
        if (response.getCommand() == VehicleGateSystemCommand.IN || response.getCommand() == VehicleGateSystemCommand.DENIED) {
            vehicleGate.setLastLicencePlateIn(response.getPlate());
            vehicleGate.setLastInDate(response.getTimestamp());
            vehicleGate.setLastInCommand(response.getCommand());
        } else if (response.getCommand() == VehicleGateSystemCommand.OUT) {
            vehicleGate.setLastLicencePlateOut(response.getPlate());
            vehicleGate.setLastOutDate(response.getTimestamp());
        }
        vehicleGateRepository.save(vehicleGate);

        // influx
        if (response.getTriggeredBy() != null) {
            VehicleGateMode mode = VehicleGateMode.ALWAYS_OPEN;
            if (!response.isAlwaysOpen()) {
                if (response.isPrivateMode()) mode = VehicleGateMode.PRIVATE_MODE;
                else mode = VehicleGateMode.PUBLIC_MODE;
            }

            HashMap<String, String> tags = new HashMap<>();
            tags.put(TRIGGERED_BY_TAG, response.getTriggeredBy());
            tags.put(MODE_TAG, mode.toString());
            influxDBQueryService.save(response.getId(), VEHICLE_GATE_DEVICE_NAME, VEHICLE_GATE_ACTION, response.getCommand().toString(), response.getTimestamp().toInstant(), tags);
        }

        // web socket
        vehicleGateMqttService.sendWsMessage(response);
    }

    private void handleSprinklerSystemMessage(String message) throws JsonProcessingException {
        SprinklerSystemMqttResponse response = objectMapper.readValue(message, SprinklerSystemMqttResponse.class);

        // database
        SprinklerSystemEntity sprinklerSystem = sprinklerSystemRepository.findById(response.getId()).orElseThrow(() -> new SmartDeviceNotFoundException("Sprinkler System not found!"));
        sprinklerSystem.setSystemOn(response.isSystemOn());
        sprinklerSystemRepository.save(sprinklerSystem);

        // influx
        if (response.getTriggeredBy() != null) {
            HashMap<String, String> tags = new HashMap<>();
            tags.put(TRIGGERED_BY_TAG, response.getTriggeredBy());
            SprinklerSystemStatus status = SprinklerSystemStatus.OFF;
            if (response.isSystemOn()) status = SprinklerSystemStatus.ON;
            influxDBQueryService.save(response.getId(), SPRINKLER_SYSTEM_DEVICE_NAME, SPRINKLER_SYSTEM_STATUS_FIELD, status.toString(), response.getTimestamp().toInstant(), tags);
        }

        // web socket
        sprinklerSystemMqttService.sendWsMessage(response);
    }

    @SneakyThrows
    private void handleElectricVehicleChargerMessage(String message) {
        ElectricVehicleChargerMqttResponse response = objectMapper.readValue(message, ElectricVehicleChargerMqttResponse.class);
        electricVehicleChargerMqttService.sendWsMessage(response);
    }

    @Override
    public void deliveryComplete(IMqttToken iMqttToken) {
        LOGGER.debug("delivery complete, message id: {}", iMqttToken.getMessageId());
    }

    @Override
    public void connectComplete(boolean b, String s) {
        LOGGER.debug("connect complete, status: {} {}", b, s);
    }

    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {
        LOGGER.debug("auth packet arrived , status: {} {}", i, mqttProperties.getAuthenticationMethod());
    }

}
