package nvt.project.smart_home.main.feature.device.air_conditioner.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.repository.AirConditionerHistoryRepository;
import nvt.project.smart_home.main.feature.device.air_conditioner.service.interf.IAirConditionerHistoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AirConditionerHistoryService implements IAirConditionerHistoryService {
    private final AirConditionerHistoryRepository airConditionerHistoryRepository;

    @Override
    public void save(AirConditionerAppointmentHistoryEntity history) {
        airConditionerHistoryRepository.save(history);
    }

    @Override
    public List<AirConditionerAppointmentHistoryEntity> findByDeviceId(long deviceId, Pageable pageable) {
        var page = airConditionerHistoryRepository.findAllByDeviceId(deviceId, pageable);
        return page.getContent();
    }
}
