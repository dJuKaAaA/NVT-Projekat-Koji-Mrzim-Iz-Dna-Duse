package nvt.project.smart_home.main.core.exception;

public class StartDateIsAfterEndDateException extends RuntimeException {
    public StartDateIsAfterEndDateException() {
        super("Start date is after end date!");
    }

    public StartDateIsAfterEndDateException(String message) {
        super(message);
    }
}
