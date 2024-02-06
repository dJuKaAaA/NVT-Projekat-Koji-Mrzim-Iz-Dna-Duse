package nvt.project.smart_home.main.core.controller;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;
import nvt.project.smart_home.main.core.service.interf.ISmartDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/smart-device")
@RestController
public class SmartDeviceController {

    private final ISmartDeviceService smartDeviceService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/for-property/{propertyId}")
    public ResponseEntity<Collection<SmartDeviceResponseDto>> getByPropertyId(@PathVariable("propertyId") Long propertyId) {
        return ResponseEntity.ok(smartDeviceService.getByPropertyId(propertyId));
    }

}
