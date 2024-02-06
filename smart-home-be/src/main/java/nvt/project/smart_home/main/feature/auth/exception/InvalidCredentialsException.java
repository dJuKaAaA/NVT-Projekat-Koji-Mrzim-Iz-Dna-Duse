package nvt.project.smart_home.main.feature.auth.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid credentials!");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
