package nvt.project.smart_home.main.feature.property.entity;

import jakarta.persistence.*;
import lombok.*;
import nvt.project.smart_home.main.core.entity.City;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.constant.PropertyType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "properties")
@Entity
public class Property {
    @Id @GeneratedValue private Long id;
    private String name;
    @ManyToOne private UserEntity owner;
    private Integer floors;
    private Double area;
    private Double longitude;
    private Double latitude;
    private String address;
    @ManyToOne private City city;
    @Enumerated(EnumType.STRING) private PropertyType type;
    @Enumerated(EnumType.STRING) private PropertyStatus status;
    private String denialReason;
    private String imageFormat;
}
