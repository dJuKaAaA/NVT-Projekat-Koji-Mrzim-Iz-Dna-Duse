package nvt.project.smart_home.main.feature.permissions.web_dto.response;

import lombok.*;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceRefResponseDto;
import nvt.project.smart_home.main.core.dto.response.UserRefResponseDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyRefResponseDto;

@Getter
@Setter
@Builder
public class PermissionResponseDto {
    long id;
    UserRefResponseDto permissionGiver;
    UserRefResponseDto permissionReceiver;
    SmartDeviceRefResponseDto device;
    PropertyRefResponseDto property;
}
