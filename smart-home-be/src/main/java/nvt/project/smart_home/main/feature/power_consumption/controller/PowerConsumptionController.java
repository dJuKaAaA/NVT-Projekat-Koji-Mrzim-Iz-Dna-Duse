package nvt.project.smart_home.main.feature.power_consumption.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.request.DatePeriodRequestDto;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.feature.power_consumption.service.interf.IPowerConsumptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/power-consumption")
@RestController
public class PowerConsumptionController {

    private final IPowerConsumptionService powerConsumptionService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-6-hours")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastSixHoursForProperty(@PathVariable("propertyId") Long propertyId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForProperty(propertyId, 6 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-12-hours")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastTwelveHoursForProperty(@PathVariable("propertyId") Long propertyId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForProperty(propertyId, 12 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-24-hours")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastTwentyFourHoursForProperty(@PathVariable("propertyId") Long propertyId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForProperty(propertyId, 24 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-week")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastWeekForProperty(@PathVariable("propertyId") Long propertyId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForProperty(propertyId, 7 * 24 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}/last-month")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastMonthForProperty(@PathVariable("propertyId") Long propertyId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForProperty(propertyId, 30 * 24 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/for-property/{propertyId}/date-period")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getBetweenDatesForProperty(@PathVariable("propertyId") Long propertyId,
                                                                             @Valid @RequestBody DatePeriodRequestDto datePeriod) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForProperty(propertyId, datePeriod.getStartDate(), datePeriod.getEndDate()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-city/{cityId}/last-6-hours")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastSixHoursForCity(@PathVariable("cityId") Long cityId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForCity(cityId, 6 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-city/{cityId}/last-12-hours")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastTwelveHoursForCity(@PathVariable("cityId") Long cityId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForCity(cityId, 12 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-city/{cityId}/last-24-hours")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastTwentyFourHoursForCity(@PathVariable("cityId") Long cityId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForCity(cityId, 24 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-city/{cityId}/last-week")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastWeekForCity(@PathVariable("cityId") Long cityId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForCity(cityId, 7 * 24 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-city/{cityId}/last-month")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getLastMonthForCity(@PathVariable("cityId") Long cityId) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForCity(cityId, 30 * 24 * 60));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/for-city/{cityId}/date-period")
    public ResponseEntity<Collection<FluxResultDto<Double>>> getBetweenDatesForCity(@PathVariable("cityId") Long cityId,
                                                                             @Valid @RequestBody DatePeriodRequestDto datePeriod) {
        return ResponseEntity.ok(powerConsumptionService.getConsumedEnergyForCity(cityId, datePeriod.getStartDate(), datePeriod.getEndDate()));
    }

}
