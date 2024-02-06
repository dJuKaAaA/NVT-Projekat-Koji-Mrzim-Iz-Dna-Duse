package nvt.project.smart_home.main.core.exception;

public class DeviceIsAlreadyOffException extends RuntimeException{
    public DeviceIsAlreadyOffException() {
        super("Device is already off!");
    }

    public DeviceIsAlreadyOffException(String message) {
        super(message);
    }
}
