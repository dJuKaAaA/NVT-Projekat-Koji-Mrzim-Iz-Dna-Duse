package nvt.project.smart_home.main.feature.device.washing_machine.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCurrentWorkMode;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.response.WashingMachineMqttResponse;
import nvt.project.smart_home.main.feature.device.washing_machine.repository.WashingMachineRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.service.interf.IWashingMachineMqttService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WashingMachineMqttService implements IWashingMachineMqttService {

    private final WashingMachineRepository washingMachineRepository;
    private final WashingMachineHistoryService historyService;

    @Override
    public void handleMqttResponse(WashingMachineMqttResponse response) {
        WashingMachineEntity entity = washingMachineRepository.findById(response.getId())
                .orElseThrow(() -> new SmartDeviceNotFoundException("Washing machine not found!"));

        String dbWorkMode = String.valueOf(entity.getWorkMode());
        String responseWorkMode = String.valueOf(response.getWorkMode());

        System.out.println("MOD U BAZI:" + dbWorkMode);
        System.out.println("DOLAZNI MOD: " + responseWorkMode);

        if (!dbWorkMode.equals(responseWorkMode)) {
            entity.setWorkMode(response.getWorkMode());
            System.out.println("##### RAZLICITI MODOVI ####");

            if (response.getWorkMode() == WashingMachineCurrentWorkMode.OFF) {
                entity.setDeviceActive(false);
                var history = WashingMachineAppointmentHistoryEntity
                        .builder()
                        .executor("WASHING MACHINE")
                        .action("OFF_%s".formatted(dbWorkMode))
                        .timestamp(DateTimeUtility.convertToLocalDateTime(response.getTimestamp()))
                        .device(entity)
                        .build();
                historyService.save(history);
            }
            else  {
                entity.setDeviceActive(true);
                var history = WashingMachineAppointmentHistoryEntity
                        .builder()
                        .executor("WASHING MACHINE")
                        .action("ON_%s".formatted(responseWorkMode))
                        .timestamp(DateTimeUtility.convertToLocalDateTime(response.getTimestamp()))
                        .device(entity)
                        .build();
                historyService.save(history);
            }
        }
        washingMachineRepository.save(entity);
    }
}
