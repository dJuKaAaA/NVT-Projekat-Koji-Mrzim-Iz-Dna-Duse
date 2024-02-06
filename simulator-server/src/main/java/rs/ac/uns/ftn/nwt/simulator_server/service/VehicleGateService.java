package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.constants.VehicleGateCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.request.VehicleGateRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.response.VehicleGateResponse;
import rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.state.VehicleGateState;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.SEND_VEHICLE_GATE_TOPIC;

@Service
@RequiredArgsConstructor
public class VehicleGateService {

    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final RunnableManager runnableManager;
    private final ObjectMapper mapper;

    private ConcurrentHashMap<Long, VehicleGateState> states = new ConcurrentHashMap<>();

    private final int GENERATE_DATA_TIME = 45;
    private final int IN_RANGE_TRIGGER = 90;
    private final int OUT_RANGE_TRIGGER = 10;
    private final int KNOWN_PLATE_TRIGGER = 10;
    private final int OPEN_GATE_TIME_IN_MILLIS = 3000;

    @PostConstruct
    public void init() {
        messageCallback.setSetVehicleGateCommand(this::setCommand);
    }

    @FunctionalInterface
    public interface Callback { void apply(VehicleGateRequest request); }

    public void setCommand(VehicleGateRequest request) {
        VehicleGateState state = states.get(request.getId());
        if (state == null) {
            setInitState(request);
            Runnable runnable = () -> simulation(request.getId());
            runnableManager.startRunnable(request.getId(), runnable, 0, GENERATE_DATA_TIME);
        } else changeStateAndCommand(request, state);
    }

    private void changeStateAndCommand(VehicleGateRequest request, VehicleGateState state) {
        if (state.isAlwaysOpen() != request.isAlwaysOpen()) changeStateAndSendChangeCommandNotification(request, state, request.isAlwaysOpen());
        else changeStateAndSendChangeCommandNotification(request, state, false);
    }

    private void setInitState(VehicleGateRequest request) {
        VehicleGateState state = VehicleGateState.builder()
                .id(request.getId())
                .isAlwaysOpen(request.isAlwaysOpen())
                .isPrivateMode(request.isPrivateMode())
                .allowedLicencePlates(request.getAllowedLicencePlates())
                .licencePlatesIn(new ArrayList<>())
                .build();
        if (state.getAllowedLicencePlates() == null) state.setAllowedLicencePlates(new ArrayList<>());
        states.put(request.getId(), state);
    }

    private void changeStateAndSendChangeCommandNotification(VehicleGateRequest request, VehicleGateState state, boolean isOpen) {
        if (request.getAllowedLicencePlates() != null) {
            state.setAllowedLicencePlates(request.getAllowedLicencePlates());
            states.put(state.getId(), state);
            return;
        }
        state.setAlwaysOpen(request.isAlwaysOpen());
        state.setPrivateMode(request.isPrivateMode());
        states.put(state.getId(), state);
        sendMqttMessage(state, isOpen, null, VehicleGateCommand.USER_CHANGE, request.getTriggeredBy());
    }

    private void simulation(long vehicleGateId) {
        VehicleGateState state = states.get(vehicleGateId);
        if (state.isAlwaysOpen()) alwaysOpenOrPublicModeTask(state);
        else {
            if (state.isPrivateMode()) privateModeTask(state);
            else alwaysOpenOrPublicModeTask(state);
        }
    }

    private void alwaysOpenOrPublicModeTask(VehicleGateState state) {
        Random random = new Random();
        int outInRand = random.nextInt(100);
        int isKnownPlate = random.nextInt(100);

        List<String> platesIn = state.getLicencePlatesIn();
        VehicleGateCommand command = VehicleGateCommand.NO_CHANGES;
        String plate = null;
        if (!state.getAllowedLicencePlates().isEmpty() && outInRand > IN_RANGE_TRIGGER) {
            if (isKnownPlate > KNOWN_PLATE_TRIGGER) plate = tryGenerateKnownPlate(state);
            else plate = generatePlate();
            platesIn.add(plate);
            command = VehicleGateCommand.IN;
        } else if (outInRand < OUT_RANGE_TRIGGER && !state.getLicencePlatesIn().isEmpty()) {
            plate = getRandomInsidePlate(state);
            platesIn.remove(plate);
            command = VehicleGateCommand.OUT;
        }

        state.setLicencePlatesIn(platesIn);
        states.put(state.getId(), state);

        if (state.isAlwaysOpen()) sendMqttMessage(state, true, plate, command, plate);
        else {
            if (command != VehicleGateCommand.NO_CHANGES) openOrCloseGate(state, plate, command);
            else sendMqttMessage(state, false, plate, command, null);
        }
    }

    private void privateModeTask(VehicleGateState state) {
        Random random = new Random();
        int outInRand = random.nextInt(100);
        int isKnownPlate = random.nextInt(100);

        List<String> platesIn = state.getLicencePlatesIn();
        VehicleGateCommand command = VehicleGateCommand.NO_CHANGES;
        String plate = null;

        if (!state.getAllowedLicencePlates().isEmpty() && outInRand > IN_RANGE_TRIGGER) {
            if (isKnownPlate > KNOWN_PLATE_TRIGGER) plate = tryGenerateKnownPlate(state);
            else plate = generatePlate();
            if (state.getAllowedLicencePlates().contains(plate)) {
                platesIn.add(plate);
                command = VehicleGateCommand.IN;
            } else command = VehicleGateCommand.DENIED;
        } else if (outInRand < OUT_RANGE_TRIGGER && !state.getLicencePlatesIn().isEmpty()) {
            plate = getRandomInsidePlate(state);
            platesIn.remove(plate);
            command = VehicleGateCommand.OUT;
        }

        if (command != VehicleGateCommand.NO_CHANGES && command != VehicleGateCommand.DENIED)
            openOrCloseGate(state, plate, command);
        else {
            String triggeredBy = null;
            if (command == VehicleGateCommand.DENIED) triggeredBy = plate;
            sendMqttMessage(state, false, plate, command, triggeredBy);
        }
    }

    @SneakyThrows
    private void sendMqttMessage(VehicleGateState state, boolean isOpen, String plate, VehicleGateCommand command, String triggeredBy) {
        VehicleGateResponse response = VehicleGateResponse.builder()
                .id(state.getId())
                .isOpen(isOpen)
                .isAlwaysOpen(state.isAlwaysOpen())
                .isPrivateMode(state.isPrivateMode())
                .plate(plate)
                .command(command)
                .triggeredBy(triggeredBy)
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_VEHICLE_GATE_TOPIC + state.getId(), new MqttMessage(mapper.writeValueAsBytes(response)));
    }

    @SneakyThrows
    private void openOrCloseGate(VehicleGateState state, String plate, VehicleGateCommand command) {
        sendMqttMessage(state, true, plate, command, plate);
        Thread.sleep(OPEN_GATE_TIME_IN_MILLIS);
        sendMqttMessage(state, false, null, VehicleGateCommand.CLOSE, "SYSTEM");
    }

    private String getRandomInsidePlate(VehicleGateState state){
        Random random = new Random();
        int plateIndex = random.nextInt(state.getLicencePlatesIn().size());
        return state.getLicencePlatesIn().get(plateIndex);
    }

    private String tryGenerateKnownPlate(VehicleGateState state) {
        Random random = new Random();
        int plateIndex = random.nextInt(state.getAllowedLicencePlates().size());
        String newPlate = state.getAllowedLicencePlates().get(plateIndex);
        if (state.getLicencePlatesIn().contains(newPlate)) newPlate = generatePlate();
        return newPlate;
    }

    public String generatePlate() {
        StringBuilder plate = new StringBuilder();
        for (int i = 0; i < 3; i++) plate.append(generateCharacter());
        plate.append("-");
        plate.append(generateCharacter());
        plate.append("-");
        for (int i = 0; i < 3; i++) plate.append(generateCharacter());
        return plate.toString();
    }

    private char generateCharacter() {
        Random rand = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int index = rand.nextInt(characters.length());
        return characters.charAt(index);
    }
}