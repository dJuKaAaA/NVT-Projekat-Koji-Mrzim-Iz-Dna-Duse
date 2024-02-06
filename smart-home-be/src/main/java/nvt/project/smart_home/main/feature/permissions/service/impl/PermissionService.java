package nvt.project.smart_home.main.feature.permissions.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceRefResponseDto;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;
import nvt.project.smart_home.main.core.dto.response.UserRefResponseDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.exception.UserNotFoundException;
import nvt.project.smart_home.main.core.mapper.SmartDeviceMapper;
import nvt.project.smart_home.main.core.repository.UserRepository;
import nvt.project.smart_home.main.core.service.interf.ISmartDeviceService;
import nvt.project.smart_home.main.core.service.interf.IUserService;
import nvt.project.smart_home.main.feature.permissions.entity.PermissionEntity;
import nvt.project.smart_home.main.feature.permissions.exceptions.PermissionNotFoundException;
import nvt.project.smart_home.main.feature.permissions.mapper.PermissionMapper;
import nvt.project.smart_home.main.feature.permissions.repository.PermissionRepository;
import nvt.project.smart_home.main.feature.permissions.service.interf.IPermissionService;
import nvt.project.smart_home.main.feature.permissions.web_dto.response.PermissionResponseDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyRefResponseDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyResponseDto;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.mapper.PropertyMapper;
import nvt.project.smart_home.main.feature.property.service.interf.IPropertyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;

    private final PermissionMapper permissionMapper;
    private final SmartDeviceMapper smartDeviceMapper;
    private final UserRepository userRepository;

    private final IPropertyService propertyService;
    private final IUserService usersService;
    private final ISmartDeviceService smartDeviceService;

    @Override
    public Collection<PropertyResponseDto> getAllObtainedProperties(String userEmail) {
        UserEntity userEntity = usersService.findByEmail(userEmail).orElseThrow(UserNotFoundException::new);

       List<Property> properties = userEntity.getObtainedPermissions().stream().map(PermissionEntity::getProperty)
               .filter(distinctByKey(Property::getId)).toList();

        return PropertyMapper.INSTANCE.propertiesToPropertyResponseDtos(properties);
    }

    @Override
    public Collection<SmartDeviceResponseDto> getAllObtainedDevicesByProperty(String userEmail, long propertyId) {
        UserEntity userEntity = usersService.findByEmail(userEmail).orElseThrow(UserNotFoundException::new);

        List<SmartDeviceEntity> propertyDevices = userEntity.getObtainedPermissions().stream()
                .map(PermissionEntity::getDevice)
                .filter(device -> device.getProperty().getId() == propertyId)
                .toList();
        return smartDeviceMapper.entitiesToResponseDtos(propertyDevices);
    }

    @Override
    public Collection<PermissionResponseDto> getAllGivenPermissions(String giverEmail) {
        UserEntity giver = usersService.findByEmail(giverEmail).orElseThrow(UserNotFoundException::new);
        return permissionMapper.entitiesToDtos(giver.getGivenPermissions());
    }

    @Transactional
    @Override
    public List<PermissionResponseDto> addPropertyPermissions(String receiverEmail, long propertyId) {
        UserEntity receiver = usersService.findByEmail(receiverEmail).orElseThrow(UserNotFoundException::new);
        Property giverProperty = propertyService.getById(propertyId).orElseThrow(PropertyNotExistsException::new);
        UserEntity giver = giverProperty.getOwner();
        List<SmartDeviceEntity> giverDevices = smartDeviceService.getAllPropertyDevices(propertyId).stream().toList();

        if(giver.equals(receiver)) throw new BadRequestException("You can't give yourself permissions!");

        List<PermissionEntity> permissionEntities = new ArrayList<>();
        for(var giverDevice: giverDevices) {
            PermissionEntity permissionEntity = PermissionEntity.builder()
                    .permissionGiver(giver)
                    .permissionReceiver(receiver)
                    .property(giverProperty)
                    .device(giverDevice)
                    .build();

            permissionEntities.add(permissionEntity);
        }

        giver.setGivenPermissions(permissionEntities);
        receiver.setObtainedPermissions(permissionEntities);

        permissionRepository.saveAll(permissionEntities);
        usersService.update(giver);
        usersService.update(receiver);

        return permissionMapper.entitiesToDtos(permissionEntities);
    }

    @Transactional
    @Override
    public PermissionResponseDto addDevicePermission(String receiverEmail, long deviceId) {
        SmartDeviceEntity giverDevice = smartDeviceService.getById(deviceId).orElseThrow(SmartDeviceNotFoundException::new);
        Property giverProperty = giverDevice.getProperty();

        UserEntity receiver = usersService.findByEmail(receiverEmail).orElseThrow(UserNotFoundException::new);
        UserEntity giver =  giverProperty.getOwner();

        if(giver.equals(receiver)) throw new BadRequestException("You can't give yourself permissions!");

        var receiverObtainedPermissions = receiver.getObtainedPermissions();
        isAlreadyDefinedPermission(receiverObtainedPermissions, giverDevice, giverProperty);

        PermissionEntity permissionEntity = PermissionEntity.builder()
                        .permissionGiver(giver)
                        .permissionReceiver(receiver)
                        .property(giverProperty)
                        .device(giverDevice)
                        .build();

        var giverGivenPermissions = giver.getGivenPermissions();
        giverGivenPermissions.add(permissionEntity);
        receiverObtainedPermissions.add(permissionEntity);

        giver.setGivenPermissions(giverGivenPermissions);
        receiver.setObtainedPermissions(receiverObtainedPermissions);

        permissionRepository.save(permissionEntity);
        return permissionMapper.entityToDto(permissionEntity);
    }

    private static void isAlreadyDefinedPermission(List<PermissionEntity> receiverObtainedPermissions, SmartDeviceEntity giverDevice, Property giverProperty) {
        for(var obtainedPermission: receiverObtainedPermissions) {
            if(obtainedPermission.getDevice().equals(giverDevice) &&
            obtainedPermission.getProperty().equals(giverProperty)) {
                throw new BadRequestException("Already defined permission for this device!");
            }
        }
    }

    @Transactional
    @Override
    public void removeAllPropertyPermissions(String userEmailToRemovePermissions, long propertyId) {
        UserEntity userToRemovePermissions = usersService.findByEmail(userEmailToRemovePermissions)
                .orElseThrow(UserNotFoundException::new);
        Property property = propertyService.getById(propertyId)
                .orElseThrow(PropertyNotExistsException::new);
        UserEntity owner = property.getOwner();

        if(userToRemovePermissions.equals(owner))
            throw new BadRequestException("You can't remove from yourself permissions!");

        owner.getGivenPermissions().removeIf(givenPermission ->
                givenPermission.getProperty().equals(property) &&
                givenPermission.getPermissionReceiver().equals(userToRemovePermissions));

        userToRemovePermissions.getObtainedPermissions().removeIf(obtainedPermission ->
                obtainedPermission.getPermissionGiver().equals(owner) &&
                obtainedPermission.getProperty().equals(property));


        usersService.update(owner);
        usersService.update(userToRemovePermissions);
    }


    @Transactional
    @Override
    public void removeDevicePermissions(String userEmailToRemovePermissions, long deviceId) {
        UserEntity userToRemovePermissions = usersService.findByEmail(userEmailToRemovePermissions)
                .orElseThrow(UserNotFoundException::new);
        SmartDeviceEntity smartDevice = smartDeviceService.getById(deviceId)
                .orElseThrow(SmartDeviceNotFoundException::new);

        Property property = smartDevice.getProperty();
        UserEntity owner = property.getOwner();

        if(userToRemovePermissions.equals(owner))
            throw new BadRequestException("You can't remove from yourself permissions!");

        owner.getGivenPermissions().removeIf(givenPermission ->
                givenPermission.getDevice().equals(smartDevice) &&
                givenPermission.getPermissionReceiver().equals(userToRemovePermissions));

        userToRemovePermissions.getObtainedPermissions().removeIf(obtainedPermission ->
                obtainedPermission.getDevice().equals(smartDevice) &&
                obtainedPermission.getPermissionGiver().equals(owner));


        usersService.update(owner);
        usersService.update(userToRemovePermissions);
    }

    @Transactional
    @Override
    public void removePermissionById(long permissionId) {
        var permission = permissionRepository.findById(permissionId).orElseThrow(PermissionNotFoundException::new);
        UserEntity permissionGiver = usersService.findByEmail(permission.getPermissionGiver().getEmail())
                        .orElseThrow(UserNotFoundException::new);
        UserEntity permissionReceiver = usersService.findByEmail(permission.getPermissionReceiver().getEmail())
                        .orElseThrow(UserNotFoundException::new);

        permissionGiver.getGivenPermissions().removeIf(givenPermission -> givenPermission.getId() == permissionId);
        usersService.update(permissionGiver);
        permissionReceiver.getObtainedPermissions().removeIf(receiverPermission -> receiverPermission.getId() == permissionId);
        usersService.update(permissionReceiver);

        permissionRepository.delete(permission);
    }

    private  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}

