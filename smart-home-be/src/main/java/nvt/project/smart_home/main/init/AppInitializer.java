package nvt.project.smart_home.main.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.RunnableManager;
import nvt.project.smart_home.main.core.constant.Role;
import nvt.project.smart_home.main.core.constant.devices.DeviceGroupType;
import nvt.project.smart_home.main.core.constant.devices.DeviceType;
import nvt.project.smart_home.main.core.dto.HeartbeatDto;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.repository.CityRepository;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.repository.UserRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.core.service.interf.IUserService;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCurrentWorkMode;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.repository.AirConditionerRepository;
import nvt.project.smart_home.main.feature.device.air_conditioner.service.impl.AirConditionerService;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCSetWorkModeWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWebRequestDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.entity.AmbientSensorEntity;
import nvt.project.smart_home.main.feature.device.ambient_sensor.repository.AmbientSensorRepository;
import nvt.project.smart_home.main.feature.device.ambient_sensor.service.impl.AmbientSensorService;
import nvt.project.smart_home.main.feature.device.home_battery.entity.HomeBatteryEntity;
import nvt.project.smart_home.main.feature.device.home_battery.repository.HomeBatteryRepository;
import nvt.project.smart_home.main.feature.device.home_battery.service.HomeBatterySimulatorService;
import nvt.project.smart_home.main.feature.device.lamp.entity.LampEntity;
import nvt.project.smart_home.main.feature.device.lamp.mqtt_dto.request.LampMqttRequest;
import nvt.project.smart_home.main.feature.device.lamp.repository.LampRepository;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemScheduleEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.request.SprinklerSystemMqttRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.request.SprinklerSystemScheduleMqttRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.repository.SprinklerSystemRepository;
import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;
import nvt.project.smart_home.main.feature.device.vehicle_gate.mqtt_dto.request.VehicleGateMqttRequest;
import nvt.project.smart_home.main.feature.device.vehicle_gate.repository.VehicleGateRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.repository.WashingMachineHistoryRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.repository.WashingMachineRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.service.impl.WashingMachineHistoryService;
import nvt.project.smart_home.main.feature.device.washing_machine.service.impl.WashingMachineService;

import nvt.project.smart_home.main.feature.permissions.service.impl.PermissionService;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.constant.PropertyType;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import nvt.project.smart_home.test_script.InsertDataScript;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.List;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.*;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.*;

@RequiredArgsConstructor
@Component
public class AppInitializer implements CommandLineRunner {

    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;

    private final RunnableManager runnableManager;
    private final HomeBatterySimulatorService homeBatterySimulatorService;

    private final SmartDeviceRepository smartDeviceRepository;
    private final HomeBatteryRepository homeBatteryRepository;
    private final AirConditionerRepository airConditionerRepository;
    private final CityRepository cityRepository;
    private final AmbientSensorRepository ambientSensorRepository;
    private final WashingMachineRepository washingMachineRepository;


    private final AirConditionerService airConditionerService;

    public static void main(String[] args) {
        var context = SpringApplication.run(InsertDataScript.class, args);
        context.close();
    }

    @Override
    public void run(String... args) {
        initAmbientSensors();
        initAirConditioners();
        initWashingMachines();
        startSimulations();
        turnOnHomeBatteries();
        startHomeBatterySimulations();
    }

    @SneakyThrows
    private void initAmbientSensors() {
        var sensors = ambientSensorRepository.findAll();
        for (var sensor: sensors) {
            if (sensor.isDeviceActive()) {
                Thread.sleep(10);
                mqttClient.subscribe(RECEIVE_AMBIENT_SENSOR_TOPIC + sensor.getId(), 2);
            }
        }
        System.out.println("AMBIENT SENSORS ACTIVATED");
    }

    @SneakyThrows
    private void initWashingMachines() {
        var washingMachines = washingMachineRepository.findAll();
        for (var washingMachine: washingMachines) {
            Thread.sleep(10);
            mqttClient.subscribe(RECEIVE_WASHING_MACHINE_TOPIC + washingMachine.getId(), 2);
        }
        System.out.println("WASHING MACHINES ACTIVATED");
    }

    @SneakyThrows
    private void initAirConditioners() {
        var airConditioners = airConditionerRepository.findAll();
        for (var airConditioner: airConditioners) {
            Thread.sleep(10);
            mqttClient.subscribe(RECEIVE_AIR_CONDITIONER_TOPIC + airConditioner.getId(), 2);

        }
    }

    @Value("${file.separator}")
    private final String separator;

    @Value("${directory.path.init.images}")
    private final String directoryInitImages;


    @SneakyThrows
    @Transactional
    public void startSimulations() {
        for (SmartDeviceEntity device : smartDeviceRepository.findAllByDeviceActive(true)) {
            // TODO: Start simulation for each device
            switch (device.getDeviceType()) {
                case DeviceType.AMBIENT_SENSOR:
                    break;
                case DeviceType.AIR_CONDITIONER:
                    break;
                case DeviceType.WASHING_MACHINE:
                    break;
                case DeviceType.LAMP:
                    LampEntity lamp = (LampEntity)device;
                    LampMqttRequest lampRequest = LampMqttRequest.builder()
                            .id(device.getId())
                            .bulbOn(lamp.isBulbOn())
                            .autoModeOn(lamp.isAutoModeOn())
                            .build();
                    mqttClient.subscribe(RECEIVE_LAMP_TOPIC + lamp.getId(), 2);
                    mqttClient.publish(SEND_LAMP_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(lampRequest)));
                    break;
                case DeviceType.VEHICLE_GATE:
                    VehicleGateEntity vehicleGate = (VehicleGateEntity)device;
                    VehicleGateMqttRequest gateRequest = VehicleGateMqttRequest.builder()
                            .id(device.getId())
                            .isAlwaysOpen(vehicleGate.isAlwaysOpen())
                            .isPrivateMode(vehicleGate.isPrivateMode())
                            .allowedLicencePlates(vehicleGate.getAllowedLicencePlates())
                            .build();
                    mqttClient.subscribe(RECEIVE_VEHICLE_GATE_TOPIC + vehicleGate.getId(), 2);
                    mqttClient.publish(SEND_VEHICLE_GATE_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(gateRequest)));
                    break;
                case DeviceType.SPRINKLER_SYSTEM:
                    SprinklerSystemEntity sprinklerSystem = (SprinklerSystemEntity)device;

                    List<SprinklerSystemScheduleMqttRequestDto> schedule = new ArrayList<>();
                    for (SprinklerSystemScheduleEntity s : sprinklerSystem.getSchedule()) {
                        schedule.add(SprinklerSystemScheduleMqttRequestDto.builder()
                                .startTime(s.getStartTime().toString())
                                .endTime(s.getEndTime().toString())
                                .days(s.getDays())
                                .build());
                    }
                    SprinklerSystemMqttRequestDto request = SprinklerSystemMqttRequestDto.builder()
                            .id(sprinklerSystem.getId())
                            .systemOn(sprinklerSystem.isSystemOn())
                            .schedule(schedule)
                            .build();
                    mqttClient.subscribe(RECEIVE_SPRINKLER_SYSTEM_TOPIC + sprinklerSystem.getId(), 2);
                    mqttClient.publish(SEND_SPRINKLER_SYSTEM_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(request)));
                    break;
                case DeviceType.SOLAR_PANEL_SYSTEM:
                    break;
                case DeviceType.HOME_BATTERY:
                    break;
                case DeviceType.ELECTRIC_VEHICLE_CHARGER:
                    break;
            }

            HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                    .deviceId(device.getId())
                    .failed(false)
                    .build();
            mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
            mqttClient.subscribe(I_AM_ALIVE_TOPIC + device.getId(), 2);
        }
    }

    private void turnOnHomeBatteries() {
        for (HomeBatteryEntity battery : homeBatteryRepository.findAllByDeviceActive(true)) {
            homeBatterySimulatorService.addBattery(battery.getProperty().getId(), battery);
        }
    }

    private void startHomeBatterySimulations() {
        Runnable batterySimRunnable = homeBatterySimulatorService::simulateOneMinutePowerConsumption;
        runnableManager.startNonEndingRunnable(batterySimRunnable, 0, 60);
    }

}
