package nvt.project.smart_home.main.feature.device.washing_machine.service.interf;

import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineAppointmentHistoryEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IWashingMachineHistoryService {
    void save(WashingMachineAppointmentHistoryEntity entity);

    List<WashingMachineAppointmentHistoryEntity> findByDeviceId(long deviceId, Pageable pageable);
}
