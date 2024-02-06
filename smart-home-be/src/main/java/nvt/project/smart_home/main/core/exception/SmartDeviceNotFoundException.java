package nvt.project.smart_home.main.core.exception;

public class SmartDeviceNotFoundException extends RuntimeException {

    public SmartDeviceNotFoundException() {
        super("Smart device not found!");
    }

    public SmartDeviceNotFoundException(String message) {
        super(message);
    }
}
