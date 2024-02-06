package nvt.project.smart_home.main.feature.device.ambient_sensor.repository;

import nvt.project.smart_home.main.feature.device.ambient_sensor.entity.AmbientSensorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface AmbientSensorRepository extends JpaRepository<AmbientSensorEntity, Long> {
    @Query("select ambientSensor from AmbientSensorEntity ambientSensor where ambientSensor.deviceActive = true")
    Collection<AmbientSensorEntity> findAllActive();
}
