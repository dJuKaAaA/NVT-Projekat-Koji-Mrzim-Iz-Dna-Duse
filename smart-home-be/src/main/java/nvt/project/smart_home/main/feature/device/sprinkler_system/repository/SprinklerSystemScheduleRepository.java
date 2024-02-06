package nvt.project.smart_home.main.feature.device.sprinkler_system.repository;

import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprinklerSystemScheduleRepository extends JpaRepository<SprinklerSystemScheduleEntity, Long> {
}
