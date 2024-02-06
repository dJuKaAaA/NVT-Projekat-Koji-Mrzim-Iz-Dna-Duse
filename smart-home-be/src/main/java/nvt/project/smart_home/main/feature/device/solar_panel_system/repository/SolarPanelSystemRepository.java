package nvt.project.smart_home.main.feature.device.solar_panel_system.repository;

import nvt.project.smart_home.main.feature.device.solar_panel_system.entity.SolarPanelSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolarPanelSystemRepository extends JpaRepository<SolarPanelSystemEntity, Long> {
}
