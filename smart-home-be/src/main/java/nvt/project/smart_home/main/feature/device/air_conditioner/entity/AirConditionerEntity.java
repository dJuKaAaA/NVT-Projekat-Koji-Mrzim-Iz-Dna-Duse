package nvt.project.smart_home.main.feature.device.air_conditioner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCurrentWorkMode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@DiscriminatorValue("AIR_CONDITIONER")
public class AirConditionerEntity extends SmartDeviceEntity {

    private double maxTemperature;

    private double minTemperature;

    @Enumerated(EnumType.STRING)
    private AirConditionerCurrentWorkMode workMode;

    @OneToMany(cascade = CascadeType.ALL)
    private List<AirConditionerWorkAppointmentEntity> workPlan = new ArrayList<>();

    @OneToMany
    private List<AirConditionerAppointmentHistoryEntity> history = new ArrayList<>();

    private Double currentWorkTemperature;
}
