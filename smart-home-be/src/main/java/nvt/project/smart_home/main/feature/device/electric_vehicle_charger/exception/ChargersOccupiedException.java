package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.exception;

public class ChargersOccupiedException extends RuntimeException {

    public ChargersOccupiedException() {
        super("All charger spots are occupied!");
    }

    public ChargersOccupiedException(String message) {
        super(message);
    }
}
