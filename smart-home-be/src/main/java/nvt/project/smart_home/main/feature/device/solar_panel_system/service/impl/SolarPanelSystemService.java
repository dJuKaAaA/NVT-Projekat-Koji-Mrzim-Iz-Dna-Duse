package nvt.project.smart_home.main.feature.device.solar_panel_system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.constant.devices.DeviceGroupType;
import nvt.project.smart_home.main.core.constant.devices.DeviceType;
import nvt.project.smart_home.main.core.dto.HeartbeatDto;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.feature.device.solar_panel_system.entity.SolarPanelEntity;
import nvt.project.smart_home.main.feature.device.solar_panel_system.entity.SolarPanelSystemEntity;
import nvt.project.smart_home.main.feature.device.solar_panel_system.exception.SolarPanelNotFoundException;
import nvt.project.smart_home.main.feature.device.solar_panel_system.mapper.SolarPanelMapper;
import nvt.project.smart_home.main.feature.device.solar_panel_system.mapper.SolarPanelSystemMapper;
import nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.request.SolarPanelMqtt;
import nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.request.SolarPanelSystemCommand;
import nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.request.SolarPanelSystemMqttRequest;
import nvt.project.smart_home.main.feature.device.solar_panel_system.repository.SolarPanelSystemRepository;
import nvt.project.smart_home.main.feature.device.solar_panel_system.service.interf.ISolarPanelSystemService;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request.SolarPanelRequestDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request.SolarPanelSystemRequestDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.response.SolarPanelSystemResponseDto;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.SOLAR_PANEL_SYSTEM_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.SOLAR_PANEL_SYSTEM_FIELD_ACTION;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TAG_KEY_PROPERTY_ID;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TAG_KEY_USER_ID;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_SOLAR_PANEL_SYSTEM_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_SOLAR_PANEL_SYSTEM_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;


@RequiredArgsConstructor
@Service
public class SolarPanelSystemService implements ISolarPanelSystemService {

    private final SolarPanelSystemRepository solarPanelSystemRepository;
    private final SolarPanelSystemMapper solarPanelSystemMapper;
    private final SolarPanelMapper solarPanelMapper;
    private final IImageService imageService;
    private final PropertyRepository propertyRepository;
    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final SmartDeviceRepository smartDeviceRepository;
    private final InfluxDBQueryService influxDBQueryService;

    @SneakyThrows
    @Override
    public SolarPanelSystemResponseDto create(SolarPanelSystemRequestDto request) {
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

        SolarPanelSystemEntity newEntity = solarPanelSystemMapper.requestDtoToEntity(request);

        newEntity.setImageFormat(image.getFormat());
        newEntity.setDeviceType(DeviceType.SOLAR_PANEL_SYSTEM);
        newEntity.setGroupType(DeviceGroupType.VEU);

        newEntity.setProperty(property);
        newEntity.setLongitude(property.getLongitude());
        newEntity.setLatitude(property.getLatitude());
        newEntity = solarPanelSystemRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        return solarPanelSystemMapper.entityToResponseDto(newEntity);
    }

    @Override
    public SolarPanelSystemResponseDto addPanel(Long id, SolarPanelRequestDto panelRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        SolarPanelSystemEntity entity = solarPanelSystemRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Solar panel system not found!"));
        entity.getSolarPanels().add(solarPanelMapper.requestDtoToEntity(panelRequest));
        entity = solarPanelSystemRepository.save(entity);
//        saveActionToInfluxDB(id, entity.getProperty().getId(), user.getId(), SolarPanelSystemAction.ADDED_PANEL);
        return solarPanelSystemMapper.entityToResponseDto(entity);
    }

    @Override
    public SolarPanelSystemResponseDto removePanel(Long id, Long panelId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        SolarPanelSystemEntity entity = solarPanelSystemRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Solar panel system not found!"));

        boolean found = false;
        for (SolarPanelEntity solarPanelEntity : entity.getSolarPanels()) {
            if (solarPanelEntity.getId().equals(panelId)) {
                entity.getSolarPanels().remove(solarPanelEntity);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new SolarPanelNotFoundException("Solar panel not found within this solar panel system!");
        }

        entity = solarPanelSystemRepository.save(entity);
//        saveActionToInfluxDB(id, entity.getProperty().getId(), user.getId(), SolarPanelSystemAction.REMOVED_PANEL);
        return solarPanelSystemMapper.entityToResponseDto(entity);
    }

    @Override
    public SolarPanelSystemResponseDto getById(Long id) {
        SolarPanelSystemEntity entity = solarPanelSystemRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Solar panel system not found!"));
        return solarPanelSystemMapper.entityToResponseDto(entity);
    }

    @SneakyThrows
    @Override
    public SolarPanelSystemResponseDto setActive(Long id, boolean active) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        SolarPanelSystemEntity entity = solarPanelSystemRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Solar panel system not found!"));

        entity.setDeviceActive(active);
        entity = solarPanelSystemRepository.save(entity);

        SolarPanelSystemMqttRequest mqttRequest = SolarPanelSystemMqttRequest.builder()
                .id(entity.getId())
                .panels(new ArrayList<>())
                .build();
        if (entity.isDeviceActive()) {
            mqttRequest.setCommand(SolarPanelSystemCommand.ON);
            for (SolarPanelEntity panelEntity : entity.getSolarPanels()) {
                mqttRequest.getPanels().add(SolarPanelMqtt.builder()
                        .area(panelEntity.getArea())
                        .efficiency(panelEntity.getEfficiency())
                        .build());
            }
            mqttRequest.setLongitude(entity.getLongitude());
            mqttRequest.setLatitude(entity.getLatitude());

            saveActionToInfluxDB(id, entity.getProperty().getId(), user.getUser().getId(), 1);
            mqttClient.subscribe(RECEIVE_SOLAR_PANEL_SYSTEM_TOPIC + entity.getId(), 2);
        } else {
            saveActionToInfluxDB(id, entity.getProperty().getId(), user.getUser().getId(), 0);
            mqttRequest.setCommand(SolarPanelSystemCommand.OFF);
        }
        mqttClient.publish(SEND_SOLAR_PANEL_SYSTEM_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));

        return solarPanelSystemMapper.entityToResponseDto(entity);
    }

    private void saveActionToInfluxDB(long id, long propertyId, long userId, int action) {
        // action = 1 -> turned on
        // action = 0 -> turned off
        action = Math.max(action, 0);
        action = Math.min(action, 1);

        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));
        tags.put(TAG_KEY_USER_ID, String.valueOf(userId));
        influxDBQueryService.save(id, SOLAR_PANEL_SYSTEM_DEVICE_NAME, SOLAR_PANEL_SYSTEM_FIELD_ACTION, action, Instant.now(), tags);
    }
}
