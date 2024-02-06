package nvt.project.smart_home.main.feature.device.lamp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.MqttConfiguration;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampValueType;
import nvt.project.smart_home.main.feature.device.lamp.service.interf.ILampHistoryService;
import nvt.project.smart_home.main.feature.device.lamp.service.interf.ILampService;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampActionHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampCommandHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampValueHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampWebResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/lamp")
@RestController
public class LampController {

    private final ILampService lampService;
    private final MqttConfiguration mqttConfiguration;
    private final ILampHistoryService historyService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public ResponseEntity<LampWebResponseDto> create(@RequestBody @Valid LampWebRequestDto request) throws IOException {
        return ResponseEntity.ok(lampService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<LampWebResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(lampService.getById(id));
    }

    @SneakyThrows
    @GetMapping("/subscribe")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> subscribe() {
        mqttConfiguration.mqttClient().subscribe("topic1", 2);
        return ResponseEntity.ok("Subscribed to thing");
    }

    @PutMapping("/{id}/bulb-on/{triggeredBy}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<LampWebResponseDto> setBulbOn(@PathVariable("id") Long id, @PathVariable("triggeredBy") String triggeredBy) {
        return ResponseEntity.ok(lampService.setBulb(id, true, triggeredBy));
    }

    @PutMapping("/{id}/bulb-off/{triggeredBy}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<LampWebResponseDto> setBulbOff(@PathVariable("id") Long id, @PathVariable("triggeredBy") String triggeredBy) {
        return ResponseEntity.ok(lampService.setBulb(id, false, triggeredBy));
    }

    @PutMapping("/{id}/auto-on/{triggeredBy}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<LampWebResponseDto> setAutoOn(@PathVariable("id") Long id, @PathVariable("triggeredBy") String triggeredBy) {
        return ResponseEntity.ok(lampService.setAuto(id, true, triggeredBy));
    }

    @PutMapping("/{id}/auto-off/{triggeredBy}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<LampWebResponseDto> setAutoOff(@PathVariable("id") Long id, @PathVariable("triggeredBy") String triggeredBy) {
        return ResponseEntity.ok(lampService.setAuto(id, false, triggeredBy));
    }

    @PostMapping("/{id}/history-light-level")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Collection<LampValueHistoryWebResponseDto> getHistoryLightLevel(@PathVariable("id") long id, @RequestBody @Valid LampHistoryWebRequestDto request) {
        return historyService.getValuesHistory(id, request, LampValueType.ILLUMINATION);
    }

    @PostMapping("/{id}/history-bulb-on")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Collection<LampValueHistoryWebResponseDto> getHistoryBulbOn(@PathVariable("id") long id, @RequestBody @Valid LampHistoryWebRequestDto request) {
        return historyService.getValuesHistory(id, request, LampValueType.BULB_ON);
    }

    @PostMapping("/{id}/history-command")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Collection<LampCommandHistoryWebResponseDto> getHistoryCommands(@PathVariable("id") long id, @RequestBody @Valid LampActionHistoryWebRequestDto request) {
        return historyService.getCommandHistory(id, request);
    }
}