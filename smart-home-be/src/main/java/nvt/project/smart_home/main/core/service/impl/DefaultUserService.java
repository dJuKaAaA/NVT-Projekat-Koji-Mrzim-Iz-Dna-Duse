package nvt.project.smart_home.main.core.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.constant.Role;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.dto.request.UserRequestDto;
import nvt.project.smart_home.main.core.dto.response.ImageResponseDto;
import nvt.project.smart_home.main.core.dto.response.UserResponseDto;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.exception.EmailAlreadyExistsException;
import nvt.project.smart_home.main.core.exception.UserNotFoundException;
import nvt.project.smart_home.main.core.mapper.ImageMapper;
import nvt.project.smart_home.main.core.mapper.UserMapper;
import nvt.project.smart_home.main.core.repository.UserRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.core.service.interf.IUserService;
import nvt.project.smart_home.main.feature.auth.dto.request.ResetPasswordDto;
import nvt.project.smart_home.main.feature.auth.exception.PasswordMismatchException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements IUserService {

    private final UserRepository userRepository;
    private final IImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

    @SneakyThrows
    @Override
    public void save(UserEntity user, ImageRequestDto image) {
        imageService.saveProfileImageToFileSystem(image);
        userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public void update(UserEntity user) {
        userRepository.save(user);
    }


    @Override
    public void updatePassword(UserEntity userEntity, String newPassword) {
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    @SneakyThrows
    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto, Role role, boolean enabled) {
        if (findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        ImageRequestDto image = userRequestDto.getProfileImage();
        image.setName(userRequestDto.getEmail());

        UserEntity admin = UserEntity.builder()
                .name(userRequestDto.getName())
                .role(role)
                .email(userRequestDto.getEmail())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .enabled(enabled)
                .build();
        save(admin, image);

        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(admin);
        ImageResponseDto imageDto = imageMapper.requestToResponse(image);
        userResponseDto.setProfileImage(imageDto);
        return userResponseDto;
    }

    @SneakyThrows
    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        UserEntity user = findByEmail(resetPasswordDto.getEmail()).orElseThrow(UserNotFoundException::new);
        String newPassword = resetPasswordDto.getNewPassword();
        String confirmationPassword = resetPasswordDto.getConfirmNewPassword();

        if (!newPassword.equals(confirmationPassword))
            throw new PasswordMismatchException("The new password and password confirmation do not match.");

        if (passwordEncoder.matches(newPassword, user.getPassword()))
            throw new PasswordMismatchException("The new password and old password can't be the same.");

        updatePassword(user, resetPasswordDto.getNewPassword());
    }

}
