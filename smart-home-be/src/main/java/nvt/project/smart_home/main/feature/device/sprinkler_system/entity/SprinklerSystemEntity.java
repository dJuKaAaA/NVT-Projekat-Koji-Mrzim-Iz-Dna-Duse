package nvt.project.smart_home.main.feature.device.sprinkler_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("SPRINKLER_SYSTEM")
public class SprinklerSystemEntity extends SmartDeviceEntity {

    private boolean systemOn;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "sprinkler_system_schedule_all_tasks",
            joinColumns = @JoinColumn(name = "sprinkler_system_id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_id"))
    private List<SprinklerSystemScheduleEntity> schedule;

}
