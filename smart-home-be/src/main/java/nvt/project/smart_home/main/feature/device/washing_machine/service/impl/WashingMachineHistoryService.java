package nvt.project.smart_home.main.feature.device.washing_machine.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.repository.WashingMachineHistoryRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.service.interf.IWashingMachineHistoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WashingMachineHistoryService implements IWashingMachineHistoryService {
    private final WashingMachineHistoryRepository washingMachineHistoryRepository;

    @Override
    public void save(WashingMachineAppointmentHistoryEntity entity) {
        washingMachineHistoryRepository.save(entity);
    }

    @Override
    public List<WashingMachineAppointmentHistoryEntity> findByDeviceId(long deviceId, Pageable pageable) {
        var page = washingMachineHistoryRepository.findAllByDeviceId(deviceId, pageable);
        return page.getContent();
    }
}
