package nvt.project.smart_home.main.feature.device.washing_machine.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WashingMachineAppointmentHistoryEntity {
    @Id
    @GeneratedValue
    private long id;

    // user email or air conditioner
    String executor;
    private String action;
    private LocalDateTime timestamp;

    @ManyToOne
    private WashingMachineEntity device;
}
