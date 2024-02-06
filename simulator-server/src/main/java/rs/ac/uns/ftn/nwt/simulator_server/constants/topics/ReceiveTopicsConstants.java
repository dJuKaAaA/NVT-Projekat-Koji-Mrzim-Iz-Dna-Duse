package rs.ac.uns.ftn.nwt.simulator_server.constants.topics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReceiveTopicsConstants {
    public static final String RECEIVE_AIR_CONDITIONER_TOPIC = "receive-air-conditioner-topic";
    public static final String RECEIVE_AMBIENT_SENSOR_TOPIC = "receive-ambient-sensor-topic";
    public static final String RECEIVE_ELECTRIC_VEHICLE_CHARGER_TOPIC = "receive-electric-vehicle-charger-topic";
    public static final String RECEIVE_HOME_BATTERY_TOPIC = "receive-home-battery-topic";
    public static final String RECEIVE_LAMP_TOPIC = "receive-lamp-topic";
    public static final String RECEIVE_SOLAR_PANEL_SYSTEM_TOPIC = "receive-solar-panel-system-topic";
    public static final String RECEIVE_SPRINKLER_SYSTEM_TOPIC = "receive-sprinkler-system-topic";
    public static final String RECEIVE_VEHICLE_GATE_TOPIC = "receive-vehicle-gate-topic";
    public static final String RECEIVE_WASHING_MACHINE_TOPIC = "receive-washing-machine-topic";
    public static final String START_HEARTBEAT = "start-heartbeat";

}
