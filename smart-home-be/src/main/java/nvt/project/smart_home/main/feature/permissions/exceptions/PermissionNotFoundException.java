package nvt.project.smart_home.main.feature.permissions.exceptions;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException() {
        super("Permission not found!");
    }

    public PermissionNotFoundException(String message) {
        super(message);
    }
}
