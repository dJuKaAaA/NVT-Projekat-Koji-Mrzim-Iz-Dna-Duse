package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.exception;

public class ChargingVehicleNotFoundException extends RuntimeException {

    public ChargingVehicleNotFoundException() {
        super("Charging vehicle not found!");
    }

    public ChargingVehicleNotFoundException(String message) {
        super(message);
    }

}
