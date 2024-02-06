package nvt.project.smart_home.main.feature.permissions.entity;

import jakarta.persistence.*;
import lombok.*;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.feature.property.entity.Property;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PermissionEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UserEntity permissionGiver;
    @ManyToOne
    private UserEntity permissionReceiver;
    @ManyToOne
    private Property property;
    @ManyToOne
    private SmartDeviceEntity device;


    @Version
    private Long version;

}
