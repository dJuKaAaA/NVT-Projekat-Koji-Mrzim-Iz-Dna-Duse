package nvt.project.smart_home.main.core.constant.topics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReceiveTopicsConstants {
    public static final String RECEIVE_AIR_CONDITIONER_TOPIC = "send-air-conditioner-topic";
    public static final String RECEIVE_AMBIENT_SENSOR_TOPIC = "send-ambient-sensor-topic";
    public static final String RECEIVE_ELECTRIC_VEHICLE_CHARGER_TOPIC = "send-electric-vehicle-charger-topic";
    public static final String RECEIVE_HOME_BATTERY_TOPIC = "send-home-battery-topic";
    public static final String RECEIVE_LAMP_TOPIC = "send-lamp-topic";
    public static final String RECEIVE_SOLAR_PANEL_SYSTEM_TOPIC = "send-solar-panel-system-topic";
    public static final String RECEIVE_SPRINKLER_SYSTEM_TOPIC = "send-sprinkler-system-topic";
    public static final String RECEIVE_VEHICLE_GATE_TOPIC = "send-vehicle-gate-topic";
    public static final String RECEIVE_WASHING_MACHINE_TOPIC = "send-washing-machine-topic";
    public static final String I_AM_ALIVE_TOPIC = "i-am-alive-topic";

}
