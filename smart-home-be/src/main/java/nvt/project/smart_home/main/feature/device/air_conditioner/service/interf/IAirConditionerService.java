package nvt.project.smart_home.main.feature.device.air_conditioner.service.interf;

import lombok.SneakyThrows;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCCancelAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCSetWorkModeWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCHistoryResponseWebDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCWebResponseDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCWorkAppointmentWebResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAirConditionerService {

    AirCWebResponseDto create(AirCWebRequestDto request);

    @SneakyThrows
    AirCWebResponseDto setCurrentWorkMode(long deviceId, AirCSetWorkModeWebRequestDto request);

    AirCWorkAppointmentWebResponseDto schedule(long deviceId, AirCWorkAppointmentWebRequestDto request);

    void cancelAppointment(long deviceId, long appointmentId, AirCCancelAppointmentWebRequestDto request);

    List<AirCHistoryResponseWebDto> getHistory(long deviceId, Pageable pageable);

    AirCWebResponseDto getById(Long id);

}
