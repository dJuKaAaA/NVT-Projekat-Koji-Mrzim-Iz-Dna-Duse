package nvt.project.smart_home.main.feature.device.solar_panel_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.request.DatePeriodRequestDto;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.service.interf.ISolarPanelSystemMqttService;
import nvt.project.smart_home.main.feature.device.solar_panel_system.service.interf.ISolarPanelSystemService;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request.SolarPanelRequestDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request.SolarPanelSystemRequestDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.response.SolarPanelSystemResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/solar-panel-system")
@RestController
public class SolarPanelSystemController {

    private final ISolarPanelSystemService solarPanelSystemService;
    private final ISolarPanelSystemMqttService solarPanelSystemMqttService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public ResponseEntity<SolarPanelSystemResponseDto> create(@RequestBody @Valid SolarPanelSystemRequestDto request)
            throws IOException {
        return ResponseEntity.ok(solarPanelSystemService.create(request));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/add-panel")
    public ResponseEntity<SolarPanelSystemResponseDto> addPanel(@PathVariable("id") Long id,
                                                                @Valid @RequestBody SolarPanelRequestDto solarPanelRequest) {
        return ResponseEntity.ok(solarPanelSystemService.addPanel(id, solarPanelRequest));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/remove-panel/{panelId}")
    public ResponseEntity<SolarPanelSystemResponseDto> addPanel(@PathVariable("id") Long id,
                                                                @PathVariable("panelId") Long panelId) {
        return ResponseEntity.ok(solarPanelSystemService.removePanel(id, panelId));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SolarPanelSystemResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(solarPanelSystemService.getById(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-active")
    public ResponseEntity<SolarPanelSystemResponseDto> setActive(@PathVariable("id") Long id) {
        return ResponseEntity.ok(solarPanelSystemService.setActive(id, true));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-inactive")
    public ResponseEntity<SolarPanelSystemResponseDto> setInactive(@PathVariable("id") Long id) {
        return ResponseEntity.ok(solarPanelSystemService.setActive(id, false));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/actions")
    public ResponseEntity<Collection<FluxResultDto<Integer>>> getActions(@PathVariable("id") Long id,
                                                                         Principal principal,
                                                                         @Valid @RequestBody DatePeriodRequestDto datePeriod) {
        return ResponseEntity.ok(solarPanelSystemMqttService.getActions(id, principal.getName(), datePeriod.getStartDate(), datePeriod.getEndDate()));
    }
}
