package nvt.project.smart_home.main.feature.device.air_conditioner.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCurrentWorkMode;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.response.AirConditionerMqttResponse;
import nvt.project.smart_home.main.feature.device.air_conditioner.repository.AirConditionerRepository;
import nvt.project.smart_home.main.feature.device.air_conditioner.service.interf.IAirConditionerMqttService;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AirConditionerMqttService implements IAirConditionerMqttService {
    private final AirConditionerRepository airConditionerRepository;
    private final AirConditionerHistoryService historyService;

    // TODO check logic
    @Override
    public void handleMqttResponse(AirConditionerMqttResponse response) {
        AirConditionerEntity airConditionerEntity = airConditionerRepository.findById(response.getId())
                .orElseThrow(() -> new SmartDeviceNotFoundException("Air conditioner not found!"));

        String dbWorkMode = String.valueOf(airConditionerEntity.getWorkMode());
        String responseWorkMode = String.valueOf(response.getWorkMode());
        System.out.println("MOD U BAZI:" + dbWorkMode);
        System.out.println("DOLAZNI MOD: " + responseWorkMode);

        if (!dbWorkMode.equals(responseWorkMode)) {
            airConditionerEntity.setWorkMode(response.getWorkMode());
            System.out.println("##### RAZLICITI MODOVI ####");
            if (response.getWorkMode() == AirConditionerCurrentWorkMode.OFF) {
                airConditionerEntity.setDeviceActive(false);
                AirConditionerAppointmentHistoryEntity history = AirConditionerAppointmentHistoryEntity.
                        builder()
                        .executor("AIR CONDITIONER")
                        .action("OFF_%s".formatted(dbWorkMode))
                        .temperature(null)
                        .timestamp(DateTimeUtility.convertToLocalDateTime(response.getTimestamp()))
                        .device(airConditionerEntity)
                        .build();
                historyService.save(history);

            } else {
                airConditionerEntity.setDeviceActive(true);
                AirConditionerAppointmentHistoryEntity history = AirConditionerAppointmentHistoryEntity.
                        builder()
                        .executor("AIR CONDITIONER")
                        .action("ON_%s".formatted(responseWorkMode))
                        .temperature(response.getTemperature())
                        .timestamp(DateTimeUtility.convertToLocalDateTime(response.getTimestamp()))
                        .device(airConditionerEntity)
                        .build();
                historyService.save(history);
            }
            airConditionerRepository.save(airConditionerEntity);
        }
    }
}
