package nvt.project.smart_home.main.core.exception;

public class StartTimeIsAfterEndTimeException extends RuntimeException {
    public StartTimeIsAfterEndTimeException() {
        super("Start time is after end time!");
    }

    public StartTimeIsAfterEndTimeException(String message) {
        super(message);
    }
}
