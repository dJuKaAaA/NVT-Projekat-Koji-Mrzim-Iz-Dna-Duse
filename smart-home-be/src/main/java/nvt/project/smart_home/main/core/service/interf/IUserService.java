package nvt.project.smart_home.main.core.service.interf;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.constant.Role;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.dto.request.UserRequestDto;
import nvt.project.smart_home.main.core.dto.response.UserResponseDto;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.feature.auth.dto.request.ResetPasswordDto;

import java.util.Optional;

public interface IUserService {
    void save(UserEntity user, ImageRequestDto image);

    Optional<UserEntity> findByEmail(String email);

    void deleteAll();

    void update(UserEntity user);

    void updatePassword(UserEntity userEntity, String newPassword);

    UserResponseDto createUser(UserRequestDto userRequestDto, Role role, boolean enabled);

    @SneakyThrows
    void resetPassword(ResetPasswordDto resetPasswordDto);

}
