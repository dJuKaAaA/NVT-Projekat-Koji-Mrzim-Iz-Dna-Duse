package nvt.project.smart_home.main.core.exception;

public class DeviceIsAlreadyOnException extends RuntimeException {
    public DeviceIsAlreadyOnException() {
        super("Device is already on!");
    }

    public DeviceIsAlreadyOnException(String message) {
        super(message);
    }
}
