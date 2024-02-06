package nvt.project.smart_home.main.feature.device.ambient_sensor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf.IAmbientSensorHistoryService;
import nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf.IAmbientSensorService;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.request.AmbientSensorHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.request.AmbientSensorWebRequestDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response.AmbientSensorHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response.AmbientSensorWebResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/ambient-sensor")
@RestController
public class AmbientSensorController {

    private final IAmbientSensorService ambientSensorService;
    private final IAmbientSensorHistoryService historyService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping
    public AmbientSensorWebResponseDto create(@RequestBody @Valid AmbientSensorWebRequestDto request) throws IOException {
        return ambientSensorService.create(request);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public AmbientSensorWebResponseDto getById(@PathVariable("id") Long id) {
        return ambientSensorService.getById(id);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-active")
    public AmbientSensorWebResponseDto setActive(@PathVariable("id") Long id) {
        return ambientSensorService.setActivity(id, true);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-inactive")
    public AmbientSensorWebResponseDto setInactive(@PathVariable("id") Long id) {
        return ambientSensorService.setActivity(id, false);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/history")
    public Collection<AmbientSensorHistoryWebResponseDto> getHistory(
            @PathVariable("id") long id,
            @RequestBody @Valid AmbientSensorHistoryWebRequestDto request) {
        return historyService.getValues(id, request);
    }
}
