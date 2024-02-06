package nvt.project.smart_home.main.feature.device.sprinkler_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sprinkler_system_scheduled_work")
@Entity
public class SprinklerSystemScheduleEntity {
    @Id
    @GeneratedValue
    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<DayOfWeek> days;
}
