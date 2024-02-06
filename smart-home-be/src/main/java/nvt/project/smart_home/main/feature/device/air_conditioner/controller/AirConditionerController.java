package nvt.project.smart_home.main.feature.device.air_conditioner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.device.air_conditioner.service.interf.IAirConditionerService;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCCancelAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCSetWorkModeWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCHistoryResponseWebDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCWebResponseDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCWorkAppointmentWebResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/air-conditioner")
@RestController
public class AirConditionerController {

    private final IAirConditionerService airConditionerService;

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public ResponseEntity<AirCWebResponseDto> create(@RequestBody @Valid AirCWebRequestDto request)
            throws IOException {
        return ResponseEntity.ok(airConditionerService.create(request));
    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AirCWebResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(airConditionerService.getById(id));
    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{deviceId}/history")
    public List<AirCHistoryResponseWebDto> getHistory(@PathVariable("deviceId") long deviceId, Pageable pageable) {
        return airConditionerService.getHistory(deviceId, pageable);
    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{deviceId}/set-current-work-mode")
    public void setCurrentWorkMode(@PathVariable("deviceId") long deviceId, @RequestBody @Valid AirCSetWorkModeWebRequestDto dto) {
        airConditionerService.setCurrentWorkMode(deviceId, dto);

    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{deviceId}/schedule")
    public AirCWorkAppointmentWebResponseDto schedule(@PathVariable("deviceId") Long deviceId, @RequestBody @Valid AirCWorkAppointmentWebRequestDto dto) {
        return airConditionerService.schedule(deviceId, dto);
    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{deviceId}/cancel-appointment/{appointmentId}")
    public void cancelAppointment(@PathVariable("deviceId") long deviceId, @PathVariable("appointmentId") long appointmentId, @RequestBody AirCCancelAppointmentWebRequestDto requestDto) {
        airConditionerService.cancelAppointment(deviceId, appointmentId, requestDto);

    }


}
