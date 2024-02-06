package nvt.project.smart_home.main.feature.device.washing_machine.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.device.washing_machine.service.interf.IWashingMachineService;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineCancelAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineSetWorkModeWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineWebResponseDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineWorkAppointmentWebResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/washing-machine")
@RestController
public class WashingMachineController {

    private final IWashingMachineService washingMachineService;

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public WashingMachineWebResponseDto create(@RequestBody @Valid WashingMachineWebRequestDto request) throws IOException {
        return washingMachineService.create(request);
    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public WashingMachineWebResponseDto getById(@PathVariable("id") Long id) {
        return washingMachineService.getById(id);
    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{deviceId}/history")
    public List<WashingMachineHistoryWebResponseDto> getHistory(@PathVariable("deviceId") long deviceId, Pageable pageable) {
        return washingMachineService.getHistory(deviceId, pageable);
    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{deviceId}/set-current-work-mode")
    public void setCurrentWorkMode(@PathVariable("deviceId") long deviceId, @RequestBody @Valid WashingMachineSetWorkModeWebRequestDto dto) {
        washingMachineService.setCurrentWorkMode(deviceId, dto);
    }

    // TEST
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{deviceId}/schedule")
    public WashingMachineWorkAppointmentWebResponseDto schedule(@PathVariable("deviceId") Long deviceId,
                                                                @RequestBody @Valid WashingMachineWorkAppointmentWebRequestDto dto) {
        return washingMachineService.schedule(deviceId, dto);
    }

    // TEST
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{deviceId}/cancel-appointment/{appointmentId}")
    public void cancelAppointment(@PathVariable("deviceId") long deviceId,
                                  @PathVariable("appointmentId") long appointmentId,
                                  @RequestBody WashingMachineCancelAppointmentWebRequestDto requestDto) {
        washingMachineService.cancelAppointment(deviceId, appointmentId, requestDto);
    }

}
