package nvt.project.smart_home.main.core.constant.influxdb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceNamesConstants {
    public static final String AIR_CONDITIONER_DEVICE_NAME = "air-conditioner";
    public static final String AMBIENT_SENSOR_DEVICE_NAME = "ambient-sensor";
    public static final String ELECTRIC_VEHICLE_CHARGER_DEVICE_NAME = "electric-vehicle-charger";
    public static final String HOME_BATTERY_DEVICE_NAME = "home-battery";
    public static final String LAMP_DEVICE_NAME = "lamp";
    public static final String SOLAR_PANEL_SYSTEM_DEVICE_NAME = "solar-panel-system";
    public static final String SPRINKLER_SYSTEM_DEVICE_NAME = "sprinkler-system";
    public static final String VEHICLE_GATE_DEVICE_NAME = "vehicle-gate";
    public static final String WASHING_MACHINE_DEVICE_NAME = "washing-machine";
    public static final String HEARTBEAT_DEVICE_NAME = "heartbeat";
    public static final String HEARTBEAT_FIELD = "am-i-failed";
}
