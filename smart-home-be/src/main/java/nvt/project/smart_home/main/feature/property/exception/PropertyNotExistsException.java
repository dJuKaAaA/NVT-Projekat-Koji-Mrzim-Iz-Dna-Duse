package nvt.project.smart_home.main.feature.property.exception;

public class PropertyNotExistsException extends RuntimeException{
    public PropertyNotExistsException() {
        super("Property not exists!");
    }
}
