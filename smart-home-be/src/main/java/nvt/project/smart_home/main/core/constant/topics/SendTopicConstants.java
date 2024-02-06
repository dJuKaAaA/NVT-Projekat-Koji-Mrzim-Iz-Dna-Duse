package nvt.project.smart_home.main.core.constant.topics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendTopicConstants {
    public static final String SEND_AIR_CONDITIONER_TOPIC = "receive-air-conditioner-topic";
    public static final String SEND_AMBIENT_SENSOR_TOPIC = "receive-ambient-sensor-topic";
    public static final String SEND_ELECTRIC_VEHICLE_CHARGER_TOPIC = "receive-electric-vehicle-charger-topic";
    public static final String SEND_HOME_BATTERY_TOPIC = "receive-home-battery-topic";
    public static final String SEND_LAMP_TOPIC = "receive-lamp-topic";
    public static final String SEND_SOLAR_PANEL_SYSTEM_TOPIC = "receive-solar-panel-system-topic";
    public static final String SEND_SPRINKLER_SYSTEM_TOPIC = "receive-sprinkler-system-topic";
    public static final String SEND_VEHICLE_GATE_TOPIC = "receive-vehicle-gate-topic";
    public static final String SEND_WASHING_MACHINE_TOPIC = "receive-washing-machine-topic";
    public static final String START_HEARTBEAT = "start-heartbeat";
}
