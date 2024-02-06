package nvt.project.smart_home.main.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.constant.devices.DeviceGroupType;
import nvt.project.smart_home.main.core.constant.devices.DeviceType;
import nvt.project.smart_home.main.feature.property.entity.Property;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class SmartDeviceEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private Property property;
    private boolean criticalFailure;
    private boolean deviceActive;
    private boolean usesBatteries;  // ako je true onda koristi baterije, a ako je false onda vuce struju
    private double powerConsumption;    // koliko trosi
    private String imageFormat;     // the full path of image is name + id + . + imageFormat
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
    @Enumerated(EnumType.STRING)
    private DeviceGroupType groupType;

    @Version
    private Long version;

}
