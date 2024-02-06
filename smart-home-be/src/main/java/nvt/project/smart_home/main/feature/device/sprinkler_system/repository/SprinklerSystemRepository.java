package nvt.project.smart_home.main.feature.device.sprinkler_system.repository;

import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprinklerSystemRepository extends JpaRepository<SprinklerSystemEntity, Long> {
}
