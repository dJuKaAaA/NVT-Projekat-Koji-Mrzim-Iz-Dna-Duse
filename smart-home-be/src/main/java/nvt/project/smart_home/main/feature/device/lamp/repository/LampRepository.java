package nvt.project.smart_home.main.feature.device.lamp.repository;

import nvt.project.smart_home.main.feature.device.lamp.entity.LampEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface LampRepository extends JpaRepository<LampEntity, Long> {

    @Query("select lamp from LampEntity lamp where lamp.deviceActive = true")
    Collection<LampEntity> findAllActive();

    @Query("UPDATE LampEntity lamp SET lamp.lightLevel = :lightLevel WHERE lamp.id = :lampId")
    void setLightLevel(Long lampId, Double lightLevel);
}
