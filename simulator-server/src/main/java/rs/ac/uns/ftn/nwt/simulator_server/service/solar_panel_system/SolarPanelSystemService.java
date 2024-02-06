package rs.ac.uns.ftn.nwt.simulator_server.service.solar_panel_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.solar_panel.SolarPanel;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.solar_panel.SolarPanelSystemRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.response.SolarPanelSystemResponse;
import rs.ac.uns.ftn.nwt.simulator_server.service.RunnableManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.SEND_SOLAR_PANEL_SYSTEM_TOPIC;

@RequiredArgsConstructor
@Service
public class SolarPanelSystemService {

    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final ObjectMapper mapper;
    private final RunnableManager runnableManager;
    private final Map<Long, Long> devices = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        messageCallback.setStartSolarPanelSystemSim(this::startSimulate);
        messageCallback.setCancelSolarPanelSystemSim(this::cancelSimulation);
    }

    public void startSimulate(SolarPanelSystemRequest request) {
        // TODO if needed add to map subscribedDevices
        Runnable runnable = () -> generateMeasurements(request);
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, 60);
        devices.put(request.getId(), runnableId);
    }

    public void cancelSimulation(SolarPanelSystemRequest request) {
        // TODO if needed remove from map subscribedDevices
        long runnableId = devices.get(request.getId());
        runnableManager.cancelRunnable(request.getId(), runnableId);
        devices.remove(request.getId());
    }

    @SneakyThrows
    private void generateMeasurements(SolarPanelSystemRequest request) {
        Location location = new Location(request.getLatitude(), request.getLongitude()); // Example: San Francisco

        Calendar target = Calendar.getInstance();
        Calendar noon = SolarPanelSystemUtils.getCalendar(target, 12, 0, 0);

        // Create a SunriseSunsetCalculator for the specified location
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, target.getTimeZone());

        // Get today's sunrise and sunset times
        Calendar sunrise = calculator.getOfficialSunriseCalendarForDate(target);
        Calendar sunset = calculator.getOfficialSunsetCalendarForDate(target);

        // Calculate the duration of sunlight in minutes
        long sunHoursInMinutes = calculateSunlightDuration(sunrise, sunset);

        double maxEnergyProduced = 0.0;
        for (SolarPanel panel : request.getPanels()) {
            double energyProduced = (double) sunHoursInMinutes / 60.0 * panel.getEfficiency() * panel.getArea();
//            energyProduced /= 1000.0;   // getting the kW
            maxEnergyProduced += energyProduced;
        }

        double energyProduced = SolarPanelSystemUtils.calculateProducedEnergy(
                sunrise,
                noon,
                target,
                sunset,
                maxEnergyProduced
        );

        SolarPanelSystemResponse generatedMeasurement = SolarPanelSystemResponse.builder()
                .id(request.getId())
                .energy(energyProduced)
                .timestamp(new Date())
                .build();
        System.out.println(generatedMeasurement.toString());
        mqttClient.publish(SEND_SOLAR_PANEL_SYSTEM_TOPIC + request.getId(),
                new MqttMessage(mapper.writeValueAsBytes(generatedMeasurement)));
    }

    private long calculateSunlightDuration(Calendar sunrise, Calendar sunset) {
        long sunriseMillis = sunrise.getTimeInMillis();
        long sunsetMillis = sunset.getTimeInMillis();

        // Calculate the duration of sunlight in minutes
        return (sunsetMillis - sunriseMillis) / (60 * 1000);
    }

    @FunctionalInterface
    public interface Callback {
        void apply(SolarPanelSystemRequest request);
    }
}
