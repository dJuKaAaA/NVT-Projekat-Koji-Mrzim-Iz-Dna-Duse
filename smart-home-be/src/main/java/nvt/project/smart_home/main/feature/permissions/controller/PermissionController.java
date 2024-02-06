package nvt.project.smart_home.main.feature.permissions.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;
import nvt.project.smart_home.main.feature.permissions.entity.PermissionEntity;
import nvt.project.smart_home.main.feature.permissions.service.impl.PermissionService;
import nvt.project.smart_home.main.feature.permissions.web_dto.response.PermissionResponseDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/permissions")
@RestController
public class PermissionController {

    private final PermissionService permissionService;

    // WORK
    @Operation(summary = "Get all cars", description = "Return all cars")
    @ApiResponse(responseCode = "200", description = "List of cars retrieved successfully!")
    @ArraySchema(schema = @Schema(implementation = PermissionEntity.class, description = "List of car objects"))
    @GetMapping("/properties/{email}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Collection<PropertyResponseDto> getAllObtainedProperties(@PathVariable("email") String userEmail) {
        return permissionService.getAllObtainedProperties(userEmail);
    }

    // WORK
    @GetMapping("/devices/{email}/{propertyId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Collection<SmartDeviceResponseDto> getAllObtainedDevicesByProperty(
            @PathVariable("email") String userEmail,
            @PathVariable("propertyId") long propertyId) {
        return permissionService.getAllObtainedDevicesByProperty(userEmail, propertyId);
    }

    // WORK
    @GetMapping("/given/{giverEmail}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Collection<PermissionResponseDto> getAllGivenPermissions(@PathVariable("giverEmail") String giverEmail) {
        return permissionService.getAllGivenPermissions(giverEmail);
    }

    // WORK
    @PostMapping("/properties/{receiverEmail}/{propertyId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Collection<PermissionResponseDto> addPropertyPermissions(@PathVariable("receiverEmail") String receiverEmail, @PathVariable("propertyId") long propertyId) {
       return permissionService.addPropertyPermissions(receiverEmail, propertyId);
    }

    @PostMapping("/devices/{receiverEmail}/{deviceId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public PermissionResponseDto addDevicePermission(
            @PathVariable("receiverEmail") String receiverEmail,
            @PathVariable("deviceId") long deviceId) {
        return permissionService.addDevicePermission(receiverEmail, deviceId);
    }

    // WORK
    @DeleteMapping("/properties/{userEmailToRemovePermissions}/{propertyId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public void removeAllPropertyPermissions(
            @PathVariable("userEmailToRemovePermissions") String userEmailToRemovePermissions,
            @PathVariable("propertyId") long propertyId) {
        permissionService.removeAllPropertyPermissions(userEmailToRemovePermissions, propertyId);
    }

    // WORK
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @DeleteMapping("/devices/{userEmailToRemovePermissions}/{deviceId}")
    public void removeDevicePermissions(
            @PathVariable("userEmailToRemovePermissions") String userEmailToRemovePermissions,
            @PathVariable("deviceId") long deviceId) {
        permissionService.removeDevicePermissions(userEmailToRemovePermissions, deviceId);
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public void removeDevicePermissions(@PathVariable long permissionId) {
        permissionService.removePermissionById(permissionId);
    }
}
