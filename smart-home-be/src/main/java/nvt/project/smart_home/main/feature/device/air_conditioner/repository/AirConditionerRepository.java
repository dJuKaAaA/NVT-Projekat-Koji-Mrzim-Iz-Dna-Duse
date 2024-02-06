package nvt.project.smart_home.main.feature.device.air_conditioner.repository;

import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCurrentWorkMode;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AirConditionerRepository extends JpaRepository<AirConditionerEntity, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE AirConditionerEntity ac SET ac.workMode = :workMode WHERE ac.id = :id")
    void updateWorkModeById(Long id, AirConditionerCurrentWorkMode workMode);

    @Modifying
    @Transactional
    @Query("UPDATE AirConditionerEntity ac SET ac.currentWorkTemperature = :temperature WHERE ac.id = :id")
    void updateCurrentWorkTemperatureById(Long id, Double temperature);
}
