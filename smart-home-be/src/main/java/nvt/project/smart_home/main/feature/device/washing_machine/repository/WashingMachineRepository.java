package nvt.project.smart_home.main.feature.device.washing_machine.repository;

import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WashingMachineRepository extends JpaRepository<WashingMachineEntity, Long> {
}
