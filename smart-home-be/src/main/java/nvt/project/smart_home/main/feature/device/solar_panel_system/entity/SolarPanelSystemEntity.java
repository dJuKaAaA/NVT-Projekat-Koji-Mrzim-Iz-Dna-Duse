package nvt.project.smart_home.main.feature.device.solar_panel_system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("SOLAR_PANEL_SYSTEM")
public class SolarPanelSystemEntity extends SmartDeviceEntity {

    @OneToMany(cascade = {CascadeType.ALL})
    private List<SolarPanelEntity> solarPanels;

    private double latitude;
    private double longitude;

}
