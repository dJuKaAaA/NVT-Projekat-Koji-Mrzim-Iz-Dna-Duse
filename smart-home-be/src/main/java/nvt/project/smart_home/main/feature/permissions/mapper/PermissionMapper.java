package nvt.project.smart_home.main.feature.permissions.mapper;

import nvt.project.smart_home.main.core.mapper.SmartDeviceMapper;
import nvt.project.smart_home.main.core.mapper.UserMapper;
import nvt.project.smart_home.main.feature.permissions.entity.PermissionEntity;
import nvt.project.smart_home.main.feature.permissions.web_dto.response.PermissionResponseDto;
import nvt.project.smart_home.main.feature.property.mapper.PropertyMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {UserMapper.class, SmartDeviceMapper.class, PropertyMapper.class})
public interface PermissionMapper {
    @Mapping(source = "id", target = "id")
    PermissionResponseDto entityToDto(PermissionEntity entity);
    List<PermissionResponseDto> entitiesToDtos(List<PermissionEntity> entities);
}
