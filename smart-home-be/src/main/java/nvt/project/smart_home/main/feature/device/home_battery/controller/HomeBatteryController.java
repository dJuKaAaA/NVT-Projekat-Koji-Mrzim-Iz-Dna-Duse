package nvt.project.smart_home.main.feature.device.home_battery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.request.DatePeriodRequestDto;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.feature.device.home_battery.dto.request.HomeBatteryRequestDto;
import nvt.project.smart_home.main.feature.device.home_battery.dto.response.HomeBatteryResponseDto;
import nvt.project.smart_home.main.feature.device.home_battery.service.interf.IHomeBatteryMqttService;
import nvt.project.smart_home.main.feature.device.home_battery.service.interf.IHomeBatteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/home-battery")
@RestController
public class HomeBatteryController {

    private final IHomeBatteryService homeBatteryService;
    private final IHomeBatteryMqttService homeBatteryMqttService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public ResponseEntity<HomeBatteryResponseDto> create(@RequestBody @Valid HomeBatteryRequestDto request)
            throws IOException {
        return ResponseEntity.ok(homeBatteryService.create(request));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<HomeBatteryResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(homeBatteryService.getById(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-6-hours/{powerConsumptionType}")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastSixHours(@PathVariable("propertyId") Long propertyId,
                                                                             @PathVariable("powerConsumptionType") String powerConsumptionType) {
        return ResponseEntity.ok(homeBatteryMqttService.getConsumedEnergy(propertyId, 6 * 60, powerConsumptionType));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-12-hours/{powerConsumptionType}")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastTwelveHours(@PathVariable("propertyId") Long propertyId,
                                                                                @PathVariable("powerConsumptionType") String powerConsumptionType) {
        return ResponseEntity.ok(homeBatteryMqttService.getConsumedEnergy(propertyId, 12 * 60, powerConsumptionType));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-24-hours/{powerConsumptionType}")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastTwentyFourHours(@PathVariable("propertyId") Long propertyId,
                                                                                    @PathVariable("powerConsumptionType") String powerConsumptionType) {
        return ResponseEntity.ok(homeBatteryMqttService.getConsumedEnergy(propertyId, 24 * 60, powerConsumptionType));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-week/{powerConsumptionType}")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastWeek(@PathVariable("propertyId") Long propertyId,
                                                                         @PathVariable("powerConsumptionType") String powerConsumptionType) {
        return ResponseEntity.ok(homeBatteryMqttService.getConsumedEnergy(propertyId, 7 * 24 * 60, powerConsumptionType));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-month/{powerConsumptionType}")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastMonth(@PathVariable("propertyId") Long propertyId,
                                                                          @PathVariable("powerConsumptionType") String powerConsumptionType) {
        return ResponseEntity.ok(homeBatteryMqttService.getConsumedEnergy(propertyId, 30 * 24 * 60, powerConsumptionType));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-hour/{powerConsumptionType}")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastHour(@PathVariable("propertyId") Long propertyId,
                                                                         @PathVariable("powerConsumptionType") String powerConsumptionType ) {
        return ResponseEntity.ok(homeBatteryMqttService.getConsumedEnergy(propertyId, 60, powerConsumptionType));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/for-property/{propertyId}/date-period/{powerConsumptionType}")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getBetweenDates(@PathVariable("propertyId") Long propertyId,
                                                                             @PathVariable("powerConsumptionType") String powerConsumptionType,
                                                                             @Valid @RequestBody DatePeriodRequestDto datePeriod) {
        return ResponseEntity.ok(homeBatteryMqttService.getConsumedEnergy(propertyId, datePeriod.getStartDate(), datePeriod.getEndDate(), powerConsumptionType));
    }

}
