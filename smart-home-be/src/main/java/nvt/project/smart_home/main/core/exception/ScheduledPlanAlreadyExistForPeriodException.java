package nvt.project.smart_home.main.core.exception;

public class ScheduledPlanAlreadyExistForPeriodException  extends RuntimeException {

    public ScheduledPlanAlreadyExistForPeriodException() {
        super("Work plan already defined for this period!");
    }

    public ScheduledPlanAlreadyExistForPeriodException(String message) {
        super(message);
    }
}
