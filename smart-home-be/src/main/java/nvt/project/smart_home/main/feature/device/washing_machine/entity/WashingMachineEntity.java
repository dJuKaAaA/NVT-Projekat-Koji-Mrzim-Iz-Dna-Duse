package nvt.project.smart_home.main.feature.device.washing_machine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCurrentWorkMode;
import org.springframework.data.jpa.repository.Lock;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("WASHING_MACHINE")
public class WashingMachineEntity extends SmartDeviceEntity {

    @Enumerated(EnumType.STRING)
    private WashingMachineCurrentWorkMode workMode;

    @OneToMany(cascade = CascadeType.ALL)
    private List<WashingMachineWorkAppointmentEntity> workPlan = new ArrayList<>();

    @OneToMany
    private List<WashingMachineAppointmentHistoryEntity> history = new ArrayList<>();

}
