package nvt.project.smart_home.main.feature.device.home_battery.repository;

import nvt.project.smart_home.main.feature.device.home_battery.entity.HomeBatteryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface HomeBatteryRepository extends JpaRepository<HomeBatteryEntity, Long> {
    @Query("select battery from HomeBatteryEntity battery where battery.deviceActive = :deviceActive")
    Collection<HomeBatteryEntity> findAllByDeviceActive(boolean deviceActive);
}
