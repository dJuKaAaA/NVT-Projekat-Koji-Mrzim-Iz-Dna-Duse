package nvt.project.smart_home.main.core.constant.influxdb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InfluxDbFieldsConstants {
    public static String AMBIENT_SENSOR_FIELD_TEMPERATURE = "temperature";
    public static String AMBIENT_SENSOR_FIELD_HUMIDITY = "humidity";
    public static String SOLAR_PANEL_SYSTEM_FIELD_ENERGY = "energy";
    public static String SOLAR_PANEL_SYSTEM_FIELD_ACTION = "solar-panel-system-action";
    public static String AIR_CONDITIONER_FIELD_TEMPERATURE = "temperature"; // TODO error?
    public static String HOME_BATTERY_FIELD_POWER_CONSUMPTION = "home-battery-power-consumption";

    public static String LAMP_VALUES_FIELD = "lamp-values";
    public static final String LAMP_ACTION_FIELD = "lamp-action";
    public static final String VEHICLE_GATE_ACTION = "vehicle-gate-action";
    public static final String SPRINKLER_SYSTEM_STATUS_FIELD = "sprinkler-system-status";

    public static final String ELECTRIC_VEHICLE_CHARGER_FIELD_ACTION = "electric-vehicle-charger-action";


    public static final String DEVICE_POWER_CONSUMPTION_FIELD = "device-power-consumption";
}

