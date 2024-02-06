package nvt.project.smart_home.main.core.exception;

public class SchedulingPlanNotFoundException extends RuntimeException {
    public SchedulingPlanNotFoundException() {
        super("Scheduling plan not found !");
    }

    public SchedulingPlanNotFoundException(String message) {
        super(message);
    }
}
