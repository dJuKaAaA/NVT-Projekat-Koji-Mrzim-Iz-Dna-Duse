package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.dto.HeartbeatDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request.ChargingVehicleRequestDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request.ElectricVehicleChargerRequestDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.response.ElectricVehicleChargerResponseDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ChargingVehicleEntity;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ElectricVehicleChargerEntity;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mapper.ChargingVehicleMapper;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mapper.ElectricVehicleChargerMapper;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.request.ChargingVehicleMqtt;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.request.ElectricVehicleChargerCommand;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.request.ElectricVehicleChargerMqttRequest;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.repository.ChargingVehicleRepository;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.repository.ElectricVehicleChargerRepository;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.constants.ElectricVehicleChargerAction;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.interf.IElectricVehicleChargerService;
import nvt.project.smart_home.main.core.constant.devices.DeviceGroupType;
import nvt.project.smart_home.main.core.constant.devices.DeviceType;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.mapper.ImageMapper;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.feature.device.home_battery.repository.HomeBatteryRepository;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import nvt.project.smart_home.main.websecurity.UserDetailsImpl;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.ELECTRIC_VEHICLE_CHARGER_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.ELECTRIC_VEHICLE_CHARGER_FIELD_ACTION;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.*;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_ELECTRIC_VEHICLE_CHARGER_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_ELECTRIC_VEHICLE_CHARGER_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;

@RequiredArgsConstructor
@Service
public class ElectricVehicleChargerService implements IElectricVehicleChargerService {

    private final ElectricVehicleChargerRepository electricVehicleChargerRepository;
    private final ElectricVehicleChargerMapper electricVehicleChargerMapper;
    private final IImageService imageService;
    private final ImageMapper imageMapper;
    private final PropertyRepository propertyRepository;
    private final HomeBatteryRepository homeBatteryRepository;
    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final SmartDeviceRepository smartDeviceRepository;
    private final ChargingVehicleMapper chargingVehicleMapper;
    private final ChargingVehicleRepository chargingVehicleRepository;
    private final InfluxDBQueryService influxDBQueryService;

    @SneakyThrows
    @Override
    public ElectricVehicleChargerResponseDto create(ElectricVehicleChargerRequestDto request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(PropertyNotExistsException::new);
        if (property.getStatus() != PropertyStatus.APPROVED) {
            throw new BadRequestException("You can only add devices to APPROVED properties!");
        }

        Optional<SmartDeviceEntity> optional = smartDeviceRepository
                .findByPropertyIdAndName(property.getId(), request.getName());
        if (optional.isPresent()) {
            throw new BadRequestException("Device with this name already exists on this property!");
        }

        ImageRequestDto image = request.getImage();
        image.setName(request.getName() + property.getId());
        imageService.saveDeviceImageToFileSystem(image);

        ElectricVehicleChargerEntity newEntity = electricVehicleChargerMapper.requestDtoToEntity(request);

        newEntity.setImageFormat(image.getFormat());
        newEntity.setDeviceType(DeviceType.ELECTRIC_VEHICLE_CHARGER);
        newEntity.setGroupType(DeviceGroupType.VEU);

        newEntity.setProperty(property);
        newEntity.setChargeLimit(100.0);
        newEntity.setChargersOccupied(0);
        newEntity.setPowerConsumption(0.0);
        newEntity.setDeviceActive(true);
        newEntity = electricVehicleChargerRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        return electricVehicleChargerMapper.entityToResponseDto(newEntity);
    }

    @Override
    public ElectricVehicleChargerResponseDto getById(long id) {
        ElectricVehicleChargerEntity entity = electricVehicleChargerRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Electric vehicle charger not found!"));
        return electricVehicleChargerMapper.entityToResponseDto(entity);
    }

    @Override
    public ElectricVehicleChargerResponseDto setChargeLimit(long id, double chargeLimit) {
        ElectricVehicleChargerEntity entity = electricVehicleChargerRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Electric vehicle charger not found!"));
        if (entity.getChargersOccupied() > 0) {
            throw new BadRequestException("You can't change the charge limit while there are vehicles being charged!");
        }

        if (chargeLimit <= 0.0 || chargeLimit > 100.0) {
            throw new BadRequestException("Charge limit must be between 0 and 100!");
        }

        entity.setChargeLimit(chargeLimit);
        entity = electricVehicleChargerRepository.save(entity);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(entity.getProperty().getId()));
        tags.put(TAG_KEY_USER_ID, String.valueOf(user.getUser().getId()));

        saveActionToInfluxDB(entity.getId(), tags, ElectricVehicleChargerAction.CHANGE_CHARGE_LIMIT);

        return electricVehicleChargerMapper.entityToResponseDto(entity);
    }

    @SneakyThrows
    @Override
    public ElectricVehicleChargerResponseDto setActive(long id, boolean active) {
        ElectricVehicleChargerEntity entity = electricVehicleChargerRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Electric vehicle charger not found!"));

        entity.setDeviceActive(active);
        entity = electricVehicleChargerRepository.save(entity);

        return electricVehicleChargerMapper.entityToResponseDto(entity);
    }

    @SneakyThrows
    @Override
    public ElectricVehicleChargerResponseDto startCharging(long id, ChargingVehicleRequestDto chargingVehicleRequest) {
        ElectricVehicleChargerEntity entity = electricVehicleChargerRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Electric vehicle charger not found!"));
        if (entity.getChargersOccupied() >= entity.getChargerCount()) {
            throw new BadRequestException("All the chargers are occupied!");
        }

        ChargingVehicleEntity chargingVehicle = chargingVehicleMapper.requestDtoToEntity(chargingVehicleRequest);
        chargingVehicle.setElectricVehicleCharger(entity);
        chargingVehicle = chargingVehicleRepository.save(chargingVehicle);

        entity.addVehicleForCharging(chargingVehicle);
        entity = electricVehicleChargerRepository.save(entity);

        ElectricVehicleChargerMqttRequest mqttRequest = ElectricVehicleChargerMqttRequest.builder()
                .id(entity.getId())
                .chargeLimit(entity.getChargeLimit())
                .command(ElectricVehicleChargerCommand.ON)
                .chargingVehicle(ChargingVehicleMqtt.builder()
                        .id(chargingVehicle.getId())
                        .currentPower(chargingVehicle.getCurrentPower())
                        .maxPower(chargingVehicle.getMaxPower())
                        .build())
                .chargePower(entity.getChargePower())
                .build();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(entity.getProperty().getId()));
        tags.put(TAG_KEY_USER_ID, String.valueOf(user.getUser().getId()));
        tags.put(CHARGING_VEHICLE_ID_TAG, String.valueOf(chargingVehicle.getId()));
        saveActionToInfluxDB(entity.getId(), tags, ElectricVehicleChargerAction.START_CHARGING);

        mqttClient.subscribe(RECEIVE_ELECTRIC_VEHICLE_CHARGER_TOPIC + "-" + entity.getId() + "_" + chargingVehicle.getId(), 2);
        mqttClient.publish(SEND_ELECTRIC_VEHICLE_CHARGER_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));

        return electricVehicleChargerMapper.entityToResponseDto(entity);
    }

    @SneakyThrows
    @Override
    public ElectricVehicleChargerResponseDto stopCharging(long id, long chargingVehicleId) {
        ElectricVehicleChargerEntity entity = electricVehicleChargerRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Electric vehicle charger not found!"));

        entity.removeVehicle(chargingVehicleId);
        entity = electricVehicleChargerRepository.save(entity);

        ElectricVehicleChargerMqttRequest mqttRequest = ElectricVehicleChargerMqttRequest.builder()
                .id(entity.getId())
                .command(ElectricVehicleChargerCommand.OFF)
                .chargingVehicle(ChargingVehicleMqtt.builder().id(chargingVehicleId).build())
                .build();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(entity.getProperty().getId()));
        tags.put(TAG_KEY_USER_ID, String.valueOf(user.getUser().getId()));
        tags.put(CHARGING_VEHICLE_ID_TAG, String.valueOf(chargingVehicleId));
        saveActionToInfluxDB(entity.getId(), tags, ElectricVehicleChargerAction.STOP_CHARGING);

        mqttClient.publish(SEND_ELECTRIC_VEHICLE_CHARGER_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));

        return electricVehicleChargerMapper.entityToResponseDto(entity);
    }

    @Override
    public ElectricVehicleChargerResponseDto addPowerToVehicle(long id, long chargingVehicleId, double power) {
        ElectricVehicleChargerEntity entity = electricVehicleChargerRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Electric vehicle charger not found!"));

        entity.addChargeToVehicle(chargingVehicleId, power);
        entity = electricVehicleChargerRepository.save(entity);
        return electricVehicleChargerMapper.entityToResponseDto(entity);
    }

    private void saveActionToInfluxDB(long id, Map<String, String> tags, ElectricVehicleChargerAction action) {
        influxDBQueryService.save(id, ELECTRIC_VEHICLE_CHARGER_DEVICE_NAME, ELECTRIC_VEHICLE_CHARGER_FIELD_ACTION, action.ordinal(), Instant.now(), tags);
    }

}
