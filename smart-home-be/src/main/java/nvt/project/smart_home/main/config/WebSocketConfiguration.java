package nvt.project.smart_home.main.config;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.config.ws_handler.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@EnableWebSocket
@Configuration
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final AmbientSensorWebSocketHandler ambientSensorWebSocketHandler;
    private final SolarPanelSystemWebSocketHandler solarPanelSystemWebSocketHandler;
    private final HomeBatteryWebSocketHandler homeBatteryWebSocketHandler;
    private final LampWebSocketHandler lampWebSocketHandler;
    private final VehicleGateWebSocketHandler vehicleGateWebSocketHandler;
    private final SprinklerSystemWebSocketHandler sprinklerSystemWebSocketHandler;
    private final ElectricVehicleChargerWebSocketHandler electricVehicleChargerWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ambientSensorWebSocketHandler, "/ambient-sensor-measurements")
                .addHandler(homeBatteryWebSocketHandler, "/home-battery-measurements")
                .addHandler(solarPanelSystemWebSocketHandler, "/solar-panel-system-measurements")
                .addHandler(lampWebSocketHandler, "/lamp-measurements")
                .addHandler(vehicleGateWebSocketHandler, "/vehicle-gate-measurements")
                .addHandler(sprinklerSystemWebSocketHandler, "/sprinkler-system-measurements")
                .addHandler(electricVehicleChargerWebSocketHandler, "/electric-vehicle-charger-measurements")
                .setAllowedOrigins("*");
    }

}
