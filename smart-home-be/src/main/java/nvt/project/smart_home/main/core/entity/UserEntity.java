package nvt.project.smart_home.main.core.entity;

import jakarta.persistence.*;
import lombok.*;
import nvt.project.smart_home.main.core.constant.Role;
import nvt.project.smart_home.main.feature.permissions.entity.PermissionEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enabled;

    @Version
    private Long version;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PermissionEntity> obtainedPermissions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<PermissionEntity> givenPermissions = new ArrayList<>();
}
