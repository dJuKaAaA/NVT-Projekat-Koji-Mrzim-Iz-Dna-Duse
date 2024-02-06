package nvt.project.smart_home.main.feature.device.vehicle_gate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf.IVehicleGateHistoryService;
import nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf.IVehicleGateService;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request.VehicleGateHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request.VehicleGateWebRequestDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateActionsHistoryResponseDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateInOutHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateWebResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/vehicle-gate")
@RestController
public class VehicleGateController {

    private final IVehicleGateService vehicleGateService;
    private final IVehicleGateHistoryService historyService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public ResponseEntity<VehicleGateWebResponseDto> create(@RequestBody @Valid VehicleGateWebRequestDto request)
            throws IOException {
        return ResponseEntity.ok(vehicleGateService.create(request));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleGateWebResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vehicleGateService.getById(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/open/{triggeredBy}")
    public ResponseEntity<VehicleGateWebResponseDto> open(@PathVariable("id") Long id, @PathVariable("triggeredBy") String triggeredBy) {
        return ResponseEntity.ok(vehicleGateService.changeIsAlwaysOpen(id, true, triggeredBy));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/close/{triggeredBy}")
    public ResponseEntity<VehicleGateWebResponseDto> close(@PathVariable("id") Long id, @PathVariable("triggeredBy") String triggeredBy) {
        return ResponseEntity.ok(vehicleGateService.changeIsAlwaysOpen(id, false, triggeredBy));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-private/{triggeredBy}")
    public ResponseEntity<VehicleGateWebResponseDto> setPrivate(@PathVariable("id") Long id, @PathVariable("triggeredBy") String triggeredBy) {
        return ResponseEntity.ok(vehicleGateService.setMode(id, true, triggeredBy));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-public/{triggeredBy}")
    public ResponseEntity<VehicleGateWebResponseDto> setPublic(@PathVariable("id") Long id, @PathVariable("triggeredBy") String triggeredBy) {
        return ResponseEntity.ok(vehicleGateService.setMode(id, false, triggeredBy));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-plate")
    public ResponseEntity<VehicleGateWebResponseDto> addAllowedLicencePlate(@PathVariable("id") Long id, @RequestBody List<String> licencePlate) {
        return ResponseEntity.ok(vehicleGateService.setAllowedLicencePlate(id, licencePlate));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/history-actions")
    public Collection<VehicleGateActionsHistoryResponseDto> getHistoryPlates(@PathVariable("id") long id, @RequestBody @Valid VehicleGateHistoryWebRequestDto request) {
        return historyService.getActionsHistory(id, request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/history-in-out")
    public Collection<VehicleGateInOutHistoryWebResponseDto> getInOutHistory(@PathVariable("id") long id, @RequestBody @Valid VehicleGateHistoryWebRequestDto request) {
        return historyService.getInOutHistory(id, request);
    }

}
