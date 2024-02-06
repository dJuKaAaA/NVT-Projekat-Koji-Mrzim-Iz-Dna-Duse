package nvt.project.smart_home.main.core.mapper;

import nvt.project.smart_home.main.core.dto.response.UserResponseDto;
import nvt.project.smart_home.main.core.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(uses = ImageMapper.class)
public interface UserMapper {

    
    UserResponseDto userToUserResponseDto(UserEntity user);
}
