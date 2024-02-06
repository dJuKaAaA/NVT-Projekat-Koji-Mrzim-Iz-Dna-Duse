package nvt.project.smart_home.main.feature.device.solar_panel_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solar_panels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolarPanelEntity {

    @Id
    @GeneratedValue
    private Long id;

    private double area;    // metri kvadratni

    private double efficiency;

}
