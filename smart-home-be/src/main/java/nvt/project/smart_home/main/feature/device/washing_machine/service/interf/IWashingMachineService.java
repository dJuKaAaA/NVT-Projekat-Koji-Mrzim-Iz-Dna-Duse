package nvt.project.smart_home.main.feature.device.washing_machine.service.interf;

import lombok.SneakyThrows;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineCancelAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineSetWorkModeWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineWebResponseDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineWorkAppointmentWebResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IWashingMachineService {


    @SneakyThrows
    WashingMachineWebResponseDto create(WashingMachineWebRequestDto request);

    @SneakyThrows
    WashingMachineWorkAppointmentWebResponseDto schedule(long deviceId, WashingMachineWorkAppointmentWebRequestDto request);

    @SneakyThrows
    void cancelAppointment(long deviceId, long appointmentId, WashingMachineCancelAppointmentWebRequestDto request);

    @SneakyThrows
    WashingMachineWebResponseDto setCurrentWorkMode(long deviceId, WashingMachineSetWorkModeWebRequestDto request);

    // DONE
    List<WashingMachineHistoryWebResponseDto> getHistory(long deviceId, Pageable pageable);

    // DONE
    WashingMachineWebResponseDto getById(Long id);
}
