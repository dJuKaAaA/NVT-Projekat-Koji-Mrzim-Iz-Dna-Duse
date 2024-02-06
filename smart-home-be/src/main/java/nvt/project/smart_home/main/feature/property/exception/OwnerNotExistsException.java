package nvt.project.smart_home.main.feature.property.exception;

public class OwnerNotExistsException extends RuntimeException {
    public OwnerNotExistsException() {
        super("Owner email not exists!");
    }
}
