package nvt.project.smart_home.main.feature.device.solar_panel_system.exception;

public class SolarPanelNotFoundException extends RuntimeException {

    public SolarPanelNotFoundException() {
        super("Solar panel not found!");
    }

    public SolarPanelNotFoundException(String message) {
        super(message);
    }
}
