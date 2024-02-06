package nvt.project.smart_home.main.feature.permissions.service.interf;

import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;
import nvt.project.smart_home.main.feature.permissions.web_dto.response.PermissionResponseDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyResponseDto;

import java.util.Collection;
import java.util.List;

public interface IPermissionService {
    Collection<PropertyResponseDto> getAllObtainedProperties(String userEmail);

    Collection<SmartDeviceResponseDto> getAllObtainedDevicesByProperty(String userEmail, long propertyId);

    Collection<PermissionResponseDto> getAllGivenPermissions(String giverEmail);

    List<PermissionResponseDto> addPropertyPermissions(String receiverEmail, long propertyId);

    PermissionResponseDto addDevicePermission(String receiverEmail, long deviceId);

    void removeAllPropertyPermissions(String userEmailToRemovePermissions, long propertyId);

    void removeDevicePermissions(String userEmailToRemovePermissions, long deviceId);

    void removePermissionById(long permissionId);
}
