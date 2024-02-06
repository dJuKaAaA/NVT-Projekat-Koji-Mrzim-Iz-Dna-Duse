package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.ambient_sensor.AmbientSensorRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.response.AmbientSensorResponse;
import rs.ac.uns.ftn.nwt.simulator_server.utils.SimulationMath;
import rs.ac.uns.ftn.nwt.simulator_server.utils.ambient_sensor.AmbientSensorDataEntry;
import rs.ac.uns.ftn.nwt.simulator_server.utils.ambient_sensor.AmbientSensorUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.SEND_AMBIENT_SENSOR_TOPIC;

@RequiredArgsConstructor
@Service
public class AmbientSensorService {

    @Value("${temp_humidity.file}")
    private final String csvFilePath;
    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final ObjectMapper mapper;
    private final RunnableManager runnableManager;
    private final Random random;

    private List<AmbientSensorDataEntry> generatedData = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Long, Long> devices = new ConcurrentHashMap<>();

    private final double LOWER_BOUND_FOR_RANDOM_GENERATOR= 0.01;
    private final double UPPER_BOUND_FOR_RANDOM_GENERATOR = 0.09;
    private final int TAKE_NEXT_ENTRY_FROM_GENERATED_DATA_IN_SECONDS = 60 * 10;
    private final int TIME_TO_GENERATE_DATA_IN_SECONDS = 5;

    // TODO add sending mqqt message response
    // TODO add back

    @PostConstruct
    public void initPredefinedSensorData() {
        messageCallback.setStartAmbientSensorSim(this::startSimulate);
        messageCallback.setCancelAmbientSensorSim(this::cancelSimulation);

        LocalDateTime now = LocalDateTime.now();
        String month = AmbientSensorUtils.formatWithLeadingZero(now.getMonthValue());
        String day = AmbientSensorUtils.formatWithLeadingZero(now.getDayOfMonth());
        String hours = AmbientSensorUtils.formatWithLeadingZero(now.getHour());
        String minutes = AmbientSensorUtils.formatWithLeadingZero(now.getMinute());

        LocalDateTime startDate = LocalDateTime.parse("2015-%s-%sT%s:%s".formatted(month,day,hours,minutes));
        LocalDateTime endDate = startDate.plusDays(1);

        generatedData = AmbientSensorUtils.readCsvDataInDateRange(csvFilePath, startDate, endDate);

        Runnable runnable = this::increaseCounter;
        runnableManager.startNonEndingRunnable(runnable,0, TAKE_NEXT_ENTRY_FROM_GENERATED_DATA_IN_SECONDS);
    }

    public void startSimulate(AmbientSensorRequest request) {
//        System.out.println("Starting ambient sensor simulation");
        Runnable runnable = ()  -> generateMeasurements(request.getId(), counter.get());
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, TIME_TO_GENERATE_DATA_IN_SECONDS);
        devices.put(request.getId(), runnableId);
    }

    public void cancelSimulation(AmbientSensorRequest request) {
        System.out.println("Kenselujem kumaro");
        long runnableId = devices.get(request.getId());
        runnableManager.cancelRunnable(request.getId(), runnableId);
        devices.remove(request.getId());
    }

    @SneakyThrows
    private void generateMeasurements(long deviceId, int counter) {

        double temperature = generatedData.get(counter).getTemperature();

        double humidity = generatedData.get(counter).getHumidity();

        double randomFactor = SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATOR, UPPER_BOUND_FOR_RANDOM_GENERATOR);

        if (random.nextBoolean()) {
            temperature += randomFactor;
            humidity += randomFactor;
        } else {
            temperature -= randomFactor;
            humidity -= randomFactor;
        }


        AmbientSensorResponse generatedMeasurement = AmbientSensorResponse.builder()
                .id(deviceId)
                .temperature(temperature)
                .humidity(humidity)
                .timestamp(new Date())
                .build();
//        System.out.println("(deviceId: %s, temperature: %s, humidity: %s)".formatted(deviceId, temperature, humidity));
        mqttClient.publish(SEND_AMBIENT_SENSOR_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(generatedMeasurement)));
    }

    private void increaseCounter() {
        int current = counter.getAndIncrement();

        if (current == generatedData.size() - 1) {
            counter.set(0);
        }
    }

    @FunctionalInterface
    public interface Callback {
        void apply(AmbientSensorRequest request);
    }

}
