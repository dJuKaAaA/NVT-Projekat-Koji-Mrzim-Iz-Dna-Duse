package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.request.DatePeriodRequestDto;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultWithTagsDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request.ChargingVehicleRequestDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request.ElectricVehicleChargerRequestDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.response.ElectricVehicleChargerResponseDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.interf.IElectricVehicleChargerMqttService;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.interf.IElectricVehicleChargerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/electric-vehicle-charger")
@RestController
public class ElectricVehicleChargerController {

    private final IElectricVehicleChargerService electricVehicleChargerService;
    private final IElectricVehicleChargerMqttService electricVehicleChargerMqttService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public ResponseEntity<ElectricVehicleChargerResponseDto> create(@RequestBody @Valid ElectricVehicleChargerRequestDto request)
            throws IOException {
        return ResponseEntity.ok(electricVehicleChargerService.create(request));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ElectricVehicleChargerResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(electricVehicleChargerService.getById(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-charge-limit/{chargeLimit}")
    public ResponseEntity<ElectricVehicleChargerResponseDto> setChargeLimit(@PathVariable("id") Long id,
                                                                            @PathVariable("chargeLimit") double chargeLimit) {
        return ResponseEntity.ok(electricVehicleChargerService.setChargeLimit(id, chargeLimit));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/start-charging")
    public ResponseEntity<ElectricVehicleChargerResponseDto> startCharging(@PathVariable("id") Long id,
                                                                           @Valid @RequestBody ChargingVehicleRequestDto chargingVehicleRequest) {
        return ResponseEntity.ok(electricVehicleChargerService.startCharging(id, chargingVehicleRequest));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/stop-charging/{chargingVehicleId}")
    public ResponseEntity<ElectricVehicleChargerResponseDto> startCharging(@PathVariable("id") Long id,
                                                                           @PathVariable("chargingVehicleId") Long chargingVehicleId) {
        return ResponseEntity.ok(electricVehicleChargerService.stopCharging(id, chargingVehicleId));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/add-power-to-vehicle/{chargingVehicleId}/{power}")
    public ResponseEntity<ElectricVehicleChargerResponseDto> addPowerToVehicle(@PathVariable("id") Long id,
                                                                               @PathVariable("chargingVehicleId") Long chargingVehicleId,
                                                                               @PathVariable("power") double power) {
        return ResponseEntity.ok(electricVehicleChargerService.addPowerToVehicle(id, chargingVehicleId, power));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/get-all-actions")
    public ResponseEntity<Collection<FluxResultWithTagsDto<Integer>>> getAllActions(@PathVariable("id") Long id,
                                                                                    @Valid @RequestBody DatePeriodRequestDto datePeriod) {
        return ResponseEntity.ok(electricVehicleChargerMqttService.getAllActions(id, datePeriod.getStartDate(), datePeriod.getEndDate()));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/get-actions/{userId}")
    public ResponseEntity<Collection<FluxResultWithTagsDto<Integer>>> getActionsByUser(@PathVariable("id") Long id,
                                                                            @PathVariable("userId") Long userId,
                                                                            @Valid @RequestBody DatePeriodRequestDto datePeriod) {
        return ResponseEntity.ok(electricVehicleChargerMqttService.getActionsByUser(id, userId, datePeriod.getStartDate(), datePeriod.getEndDate()));
    }
}
