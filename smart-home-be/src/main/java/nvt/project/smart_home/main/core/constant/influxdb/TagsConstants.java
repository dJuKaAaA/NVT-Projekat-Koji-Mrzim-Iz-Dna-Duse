package nvt.project.smart_home.main.core.constant.influxdb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagsConstants {

    public static final String TAG_KEY_POWER_CONSUMPTION_TYPE = "power-consumption-type";
    public static final String TAG_KEY_POWER_CONSUMPTION_CONSUMED = "power-consumption-consumed";
    public static final String TAG_KEY_POWER_CONSUMPTION_GENERATED = "power-consumption-generated";
    public static final String HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE = "battery-energy-consumption-type";
    public static final String HOME_BATTERY_TAG_VALUE_CONSUMED_CONSUMED = "consumed-from-battery";
    public static final String HOME_BATTERY_TAG_VALUE_CONSUMED_GENERATED = "generated-to-battery";
    public static final String TAG_VALUE_CONSUMED_RETURNED_TO_NETWORK = "returned-to-network";
    public static final String TAG_VALUE_CONSUMED_TAKEN_FROM_NETWORK = "taken-from-network";
    public static final String TAG_KEY_PROPERTY_ID = "propertyId";
    public static final String TAG_KEY_CITY_ID = "cityId";
    public static final String TAG_KEY_USER_ID = "userId";
    public static final String LAMP_COMMAND_TAG = "lamp_command";
    public static final String LAMP_VALUE_TAG = "lamp_value";


    public static final String VEHICLE_GATE_PLATE_TAG = "plate";
    public static final String SYSTEM_COMMAND_TAG = "system_command";
    public static final String USER_COMMAND_TAG = "user_command";
    public static final String TRIGGERED_BY_TAG = "triggered_by";
    public static final String MODE_TAG = "mode";

    public static final String CHARGING_VEHICLE_ID_TAG = "chargingVehicleId";

}
