package nvt.project.smart_home.main.feature.device.home_battery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ChargingVehicleEntity;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ElectricVehicleChargerEntity;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.repository.ChargingVehicleRepository;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.repository.ElectricVehicleChargerRepository;
import nvt.project.smart_home.main.config.ws_handler.HomeBatteryWebSocketHandler;
import nvt.project.smart_home.main.core.constant.devices.DeviceType;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.feature.device.home_battery.constants.PowerConsumptionType;
import nvt.project.smart_home.main.feature.device.home_battery.entity.HomeBatteryEntity;
import nvt.project.smart_home.main.feature.device.home_battery.mqtt_dto.HomeBatteryMqttDto;
import nvt.project.smart_home.main.feature.device.home_battery.repository.HomeBatteryRepository;
import nvt.project.smart_home.main.feature.device.solar_panel_system.service.interf.ISolarPanelSystemMqttService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.DEVICE_POWER_CONSUMPTION_FIELD;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.HOME_BATTERY_FIELD_POWER_CONSUMPTION;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.*;

@RequiredArgsConstructor
@Service
public class HomeBatterySimulatorService {

    private final Map<Long, Set<HomeBatteryEntity>> batteriesOnline = new HashMap<>();
    private final InfluxDBQueryService influxDBQueryService;
    private final HomeBatteryRepository homeBatteryRepository;
    private final SmartDeviceRepository smartDeviceRepository;
    private final HomeBatteryWebSocketHandler homeBatteryWebSocketHandler;
    private final ObjectMapper objectMapper;
    private final ISolarPanelSystemMqttService solarPanelSystemMqttService;
    private final ElectricVehicleChargerRepository electricVehicleChargerRepository;
    private final ChargingVehicleRepository chargingVehicleRepository;

    public void addBattery(long propertyId, HomeBatteryEntity homeBattery) {
        if (!batteriesOnline.containsKey(propertyId)) {
            batteriesOnline.put(propertyId, new HashSet<>());
        }
        batteriesOnline.get(propertyId).add(homeBattery);
    }

    private PowerConsumptionType consumePowerFromBatteries(long propertyId, double powerConsumed) {
        // if the device uses batteries, then it will not drain power from the home battery or the distribution
        if (!batteriesOnline.containsKey(propertyId)) {
            return PowerConsumptionType.NONE;
        }

        // initializing the tags for writing to influxdb
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));
        tags.put(HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE, HOME_BATTERY_TAG_VALUE_CONSUMED_CONSUMED);

        Set<HomeBatteryEntity> homeBatteries = batteriesOnline.get(propertyId);
        double excessPower = 0.0;
        boolean hasExcess = false;
        for (HomeBatteryEntity battery : homeBatteries) {
            double currentPower;
            if (hasExcess) {
                currentPower = battery.getCurrent() - excessPower;
            } else {
                currentPower = battery.getCurrent() - powerConsumed;
            }
            if (currentPower < 0.0) {
                influxDBQueryService.save(HOME_BATTERY_DEVICE_NAME, HOME_BATTERY_FIELD_POWER_CONSUMPTION, battery.getCurrent(), Instant.now(), tags);
                battery.setCurrent(0.0);
                excessPower = Math.abs(currentPower);
                hasExcess = true;
            } else if (currentPower > battery.getCapacity()) {
                influxDBQueryService.save(HOME_BATTERY_DEVICE_NAME, HOME_BATTERY_FIELD_POWER_CONSUMPTION, battery.getCurrent() - battery.getCapacity(), Instant.now(), tags);
                battery.setCurrent(battery.getCapacity());
                excessPower = battery.getCapacity() - currentPower;
                hasExcess = true;
            } else {
                battery.setCurrent(currentPower);
                if (hasExcess) {
                    powerConsumed = excessPower;
                }
                influxDBQueryService.save(HOME_BATTERY_DEVICE_NAME, HOME_BATTERY_FIELD_POWER_CONSUMPTION, powerConsumed, Instant.now(), tags);
                hasExcess = false;
                break;
            }
            homeBatteryRepository.save(battery);
        }

        if (hasExcess) {
            Map<String, String> excessTags = new HashMap<>();
            excessTags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));
            excessTags.put(HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE, TAG_VALUE_CONSUMED_TAKEN_FROM_NETWORK);
            influxDBQueryService.save(HOME_BATTERY_DEVICE_NAME, HOME_BATTERY_FIELD_POWER_CONSUMPTION, excessPower, Instant.now(), excessTags);
            return PowerConsumptionType.TAKEN_FROM_NETWORK;
        }

        return PowerConsumptionType.CONSUMED;
    }

    @SneakyThrows
    public void simulateOneMinutePowerConsumption() {
        Map<Long, Double> consumption = new HashMap<>();
        Map<Long, Long> propertyIdToCityId = new HashMap<>();
        for (SmartDeviceEntity device : smartDeviceRepository.findAllByDeviceActive(true)) {
            if (device.isUsesBatteries()) {
                continue;
            }

            long propertyId = device.getProperty().getId();
            if (!consumption.containsKey(propertyId)) {
                consumption.put(device.getProperty().getId(), 0.0);
            }

            double consumedEnergy;
            if (device.getDeviceType() == DeviceType.SOLAR_PANEL_SYSTEM) {
                Collection<FluxResultDto<Double>> powerGenerated = solarPanelSystemMqttService
                        .getEnergyProduced(device.getId(), 1);
                double sum = 0;
                for (FluxResultDto<Double> value : powerGenerated) {
                    sum += value.getValue();
                }
                consumedEnergy = -sum;
            } else if (device.getDeviceType() == DeviceType.ELECTRIC_VEHICLE_CHARGER) {
                ElectricVehicleChargerEntity electricVehicleCharger = electricVehicleChargerRepository.findById(device.getId())
                        .orElseThrow(() -> new SmartDeviceNotFoundException("Could not fetch electric vehicle from database even though the device type is ELECTRIC_VEHICLE_CHARGER - INTERNAL ERROR"));

                double chargeConsumption = 0.0;
                for (ChargingVehicleEntity chargingVehicle : chargingVehicleRepository.findByElectricVehicleCharger(electricVehicleCharger)) {
                    double vehicleChargePercentage = chargingVehicle.getCurrentPower() / chargingVehicle.getMaxPower();
                    if (vehicleChargePercentage < electricVehicleCharger.getChargeLimit()) {
                        chargeConsumption += electricVehicleCharger.getChargePower();
                    }
                }

                consumedEnergy = chargeConsumption;
            } else {
                consumedEnergy = device.getPowerConsumption() / 60.0;
            }

            System.out.println("Start logging power consumption...");
            consumption.put(propertyId, consumption.get(propertyId) + consumedEnergy);
            if (consumedEnergy != 0.0) {
                Map<String, String> tags = new HashMap<>();
                tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));

                if (!propertyIdToCityId.containsKey(propertyId)) {
                    long cityId = device.getProperty().getCity().getId();
                    propertyIdToCityId.put(propertyId, cityId);
                }
                tags.put(TAG_KEY_CITY_ID, String.valueOf(propertyIdToCityId.get(propertyId)));
                if (consumedEnergy < 0) {
                    tags.put(TAG_KEY_POWER_CONSUMPTION_TYPE, TAG_KEY_POWER_CONSUMPTION_GENERATED);
                } else {
                    tags.put(TAG_KEY_POWER_CONSUMPTION_TYPE, TAG_KEY_POWER_CONSUMPTION_CONSUMED);
                }
                influxDBQueryService.save(device.getId(), getDeviceName(device.getDeviceType()), DEVICE_POWER_CONSUMPTION_FIELD, consumedEnergy, Instant.now(), tags);
                System.out.println("Logged power consumption");
            }
        }

        for (Long propertyId : consumption.keySet()) {
            PowerConsumptionType powerConsumptionType = consumePowerFromBatteries(propertyId, consumption.get(propertyId));
            HomeBatteryMqttDto mqttDto = HomeBatteryMqttDto.builder()
                    .propertyId(propertyId)
                    .powerConsumed(consumption.get(propertyId))
                    .timestamp(Instant.now())
                    .powerConsumptionType(powerConsumptionType)
                    .build();
            String message = objectMapper.writeValueAsString(mqttDto);
            homeBatteryWebSocketHandler.broadcastMessage(propertyId, message);
        }
    }

    public String getDeviceName(DeviceType deviceType) {
        String deviceTypeConstant = "N/A";
        switch (deviceType) {
            case DeviceType.HOME_BATTERY -> deviceTypeConstant = HOME_BATTERY_DEVICE_NAME;
            case DeviceType.AIR_CONDITIONER -> deviceTypeConstant = AIR_CONDITIONER_DEVICE_NAME;
            case DeviceType.AMBIENT_SENSOR -> deviceTypeConstant = AMBIENT_SENSOR_DEVICE_NAME;
            case DeviceType.ELECTRIC_VEHICLE_CHARGER -> deviceTypeConstant = ELECTRIC_VEHICLE_CHARGER_DEVICE_NAME;
            case DeviceType.LAMP -> deviceTypeConstant = LAMP_DEVICE_NAME;
            case DeviceType.SOLAR_PANEL_SYSTEM -> deviceTypeConstant = SOLAR_PANEL_SYSTEM_DEVICE_NAME;
            case DeviceType.SPRINKLER_SYSTEM -> deviceTypeConstant = SPRINKLER_SYSTEM_DEVICE_NAME;
            case DeviceType.VEHICLE_GATE -> deviceTypeConstant = VEHICLE_GATE_DEVICE_NAME;
            case DeviceType.WASHING_MACHINE -> deviceTypeConstant = WASHING_MACHINE_DEVICE_NAME;
        }

        return deviceTypeConstant;
    }
}


