package nvt.project.smart_home.main.feature.permissions.repository;

import nvt.project.smart_home.main.feature.permissions.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
}
