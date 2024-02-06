package nvt.project.smart_home.main.feature.device.air_conditioner.entity;

import jakarta.persistence.*;
import lombok.*;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCommand;

import java.time.LocalTime;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AirConditionerWorkAppointmentEntity {

    @Id
    @GeneratedValue
    long id;
    @ManyToOne
    private UserEntity bookedByUser;
    private LocalTime startTime;
    private LocalTime endTime;
    @Enumerated(EnumType.STRING)
    private AirConditionerCommand command;
    private double wantedTemperature;
}
