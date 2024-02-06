package nvt.project.smart_home.main.core.repository;

import nvt.project.smart_home.main.core.entity.ScheduledWorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledWorkRepository extends JpaRepository<ScheduledWorkEntity, Long> {
}
