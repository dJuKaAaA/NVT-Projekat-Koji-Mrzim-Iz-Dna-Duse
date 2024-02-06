package nvt.project.smart_home.main.core.repository;

import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface SmartDeviceRepository extends JpaRepository<SmartDeviceEntity, Long> {
    Collection<SmartDeviceEntity> findAllByPropertyId(Long propertyId);
    Optional<SmartDeviceEntity> findByPropertyIdAndName(Long propertyId, String name);
    @Query("select device from SmartDeviceEntity device where device.deviceActive = :deviceActive")
    Collection<SmartDeviceEntity> findAllByDeviceActive(boolean deviceActive);
}
