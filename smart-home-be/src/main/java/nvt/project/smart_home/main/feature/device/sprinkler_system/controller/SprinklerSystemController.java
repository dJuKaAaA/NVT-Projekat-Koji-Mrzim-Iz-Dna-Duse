package nvt.project.smart_home.main.feature.device.sprinkler_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf.ISprinklerSystemHistoryService;
import nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf.ISprinklerSystemService;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SetScheduleRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SetSystemOnOffRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SprinklerSystemHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SprinklerSystemRequestWebDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response.SprinklerSystemHistoryResponseWebDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response.SprinklerSystemResponseWebDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/sprinkler-system")
@RestController
public class SprinklerSystemController {

    private final ISprinklerSystemService sprinklerSystemService;
    private final ISprinklerSystemHistoryService historyService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("")
    public ResponseEntity<SprinklerSystemResponseWebDto> create(@RequestBody @Valid SprinklerSystemRequestWebDto request) throws IOException {
        return ResponseEntity.ok(sprinklerSystemService.create(request));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SprinklerSystemResponseWebDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(sprinklerSystemService.getById(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/set-on-off")
    public ResponseEntity<SprinklerSystemResponseWebDto> systemOnOff(@PathVariable("id") Long id, @RequestBody SetSystemOnOffRequestDto setSystemOnOffRequestDto) {
        return ResponseEntity.ok(sprinklerSystemService.setSystemOn(id, setSystemOnOffRequestDto));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/set-schedule")
    public ResponseEntity<SprinklerSystemResponseWebDto> setSchedule(@RequestBody @Valid SetScheduleRequestDto scheduledWorkRequest) {
        return ResponseEntity.ok(sprinklerSystemService.setSchedule(scheduledWorkRequest));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("{id}/history-actions")
    public Collection<SprinklerSystemHistoryResponseWebDto> getHistoryOfActions(@PathVariable("id") long id, @RequestBody @Valid SprinklerSystemHistoryWebRequestDto request) {
        return historyService.getHistoryOfActions(id, request);
    }

}
