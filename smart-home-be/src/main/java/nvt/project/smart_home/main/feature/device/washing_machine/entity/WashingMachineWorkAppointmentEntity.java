package nvt.project.smart_home.main.feature.device.washing_machine.entity;

import jakarta.persistence.*;
import lombok.*;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCommand;

import java.time.LocalTime;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WashingMachineWorkAppointmentEntity {

    @Id
    @GeneratedValue
    long id;
    @ManyToOne
    private UserEntity bookedByUser;

    private LocalTime startTime;
    private LocalTime endTime;
    @Enumerated(EnumType.STRING)
    private WashingMachineCommand command;
}
