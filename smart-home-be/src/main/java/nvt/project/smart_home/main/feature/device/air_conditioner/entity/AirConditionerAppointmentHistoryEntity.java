package nvt.project.smart_home.main.feature.device.air_conditioner.entity;

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
public class AirConditionerAppointmentHistoryEntity {
    @Id
    @GeneratedValue
    private long id;

    // user email or air conditioner
    String executor;
    private String action;
    private LocalDateTime timestamp;
    private Double temperature;
    
    @ManyToOne
    private AirConditionerEntity device;
}
