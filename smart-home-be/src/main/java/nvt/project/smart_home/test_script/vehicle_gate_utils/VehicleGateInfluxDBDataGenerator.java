package nvt.project.smart_home.test_script.vehicle_gate_utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.feature.device.vehicle_gate.constants.VehicleGateMode;
import nvt.project.smart_home.main.feature.device.vehicle_gate.constants.VehicleGateSystemCommand;
import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.VEHICLE_GATE_ACTION;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.MODE_TAG;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TRIGGERED_BY_TAG;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleGateInfluxDBDataGenerator {
    private VehicleGateEntity vehicleGate;
    private InfluxDBQueryService influxDBQueryService;
    private List<String> vehiclePlatesIn = new ArrayList<>();
    private List<String> notAllowedVehiclePlates = new ArrayList<>();
    private boolean isPrivateMode;

    private final int NUM_OF_DAYS = 90;
    private final int GENERATE_TIME_SEC = 45;
    private final String TRIGGERED_BY_SYSTEM = "SYSTEM";

    public void generateData() {
        System.out.println("### Start InfluxDB generator for: " + vehicleGate.getName());
        generateNotAllowedVehiclePlates();
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(NUM_OF_DAYS);

        boolean failed = false;
        int counter = 0;
        List<Integer> mountsOfFails = new ArrayList<>();

        while (startTime.isBefore(endTime)) {

            // offline
            if (!failed && !(Duration.between(startTime, endTime).toDays() < 5) && !mountsOfFails.contains(startTime.getMonthValue())) {
                failed = checkIfFailed();
                if (failed) {
                    mountsOfFails.add(startTime.getMonthValue());
                    System.out.println("START F: " + startTime);
                }
            }
            if (failed && counter == 1000) {
                failed = false;
                counter = 0;
                System.out.println("STOP F: " + startTime);
            }
            if (failed) {
                counter++;
                startTime = startTime.plusSeconds(GENERATE_TIME_SEC);
                continue;
            }

            // online
            putHeartbeatInInflux(startTime);
            if ((startTime.getDayOfWeek().getValue() > 5) != isPrivateMode){
                isPrivateMode = !isPrivateMode;
                changeCommandByUser(startTime);
            }
            if (isPrivateMode) generatePrivateModeData(startTime);
            else generatePublicModeData(startTime);
            startTime = startTime.plusSeconds(GENERATE_TIME_SEC);
        }

    }

    private void generatePrivateModeData(LocalDateTime time) {
        int inOutRandom = generateRandomNumberInRange(1, 10000);
        int knownPlate = generateRandomNumberInRange(1, 100);

        String plate;
        if (inOutRandom > 9997) {
            if (knownPlate > 85) {
                plate = pickRandomFromAllowedVehiclePlates();
                if (plate != null) {
                    putDataInInflux(VehicleGateSystemCommand.IN, VehicleGateMode.PRIVATE_MODE, plate, time);
                    putDataInInflux(VehicleGateSystemCommand.CLOSE, VehicleGateMode.PRIVATE_MODE, TRIGGERED_BY_SYSTEM, time.plusSeconds(GENERATE_TIME_SEC - 1));
                }
            } else {
                plate = pickRandomFromNotAllowedVehiclePlates(true);
                if (plate != null) {
                    putDataInInflux(VehicleGateSystemCommand.DENIED, VehicleGateMode.PRIVATE_MODE, plate, time);
                }
            }
        } else if (inOutRandom < 4) goOut(time, VehicleGateMode.PRIVATE_MODE);
    }

    private void generatePublicModeData(LocalDateTime time) {
        int inOutRandom = generateRandomNumberInRange(1, 10000);
        int knownPlate = generateRandomNumberInRange(1, 100);

        String plate;
        if (inOutRandom > 9997) {
            if (knownPlate > 85) plate = pickRandomFromAllowedVehiclePlates();
            else plate = pickRandomFromNotAllowedVehiclePlates(false);
            if (plate != null) {
                putDataInInflux(VehicleGateSystemCommand.IN, VehicleGateMode.PUBLIC_MODE, plate, time);
                putDataInInflux(VehicleGateSystemCommand.CLOSE, VehicleGateMode.PUBLIC_MODE, TRIGGERED_BY_SYSTEM, time.plusSeconds(GENERATE_TIME_SEC - 1));
            }
        } else if (inOutRandom < 4) goOut(time, VehicleGateMode.PUBLIC_MODE);
    }

    private void goOut(LocalDateTime time, VehicleGateMode mode) {
        String plate = pickRandomFromInVehiclePlates();
        if (plate != null) {
            putDataInInflux(VehicleGateSystemCommand.OUT, mode, plate, time);
            putDataInInflux(VehicleGateSystemCommand.CLOSE, mode, TRIGGERED_BY_SYSTEM, time.plusSeconds(GENERATE_TIME_SEC - 1));
        }
    }

    private void changeCommandByUser(LocalDateTime time) {
        putDataInInflux(VehicleGateSystemCommand.USER_CHANGE, isPrivateMode ? VehicleGateMode.PRIVATE_MODE : VehicleGateMode.PUBLIC_MODE, vehicleGate.getProperty().getOwner().getEmail(), time);
    }

    private String pickRandomFromAllowedVehiclePlates() {
        if (!vehicleGate.getAllowedLicencePlates().isEmpty()) {
            int randomIndex = generateRandomNumberInRange(0, vehicleGate.getAllowedLicencePlates().size() - 1);
            String plate = vehicleGate.getAllowedLicencePlates().get(randomIndex);
            if (vehiclePlatesIn.contains(plate)) return null;
            vehiclePlatesIn.add(plate);
            return plate;
        }
        return null;
    }

    private String pickRandomFromNotAllowedVehiclePlates(boolean isDenied) {
        if (!notAllowedVehiclePlates.isEmpty()) {
            int randomIndex = generateRandomNumberInRange(0, notAllowedVehiclePlates.size() - 1);
            String plate = notAllowedVehiclePlates.get(randomIndex);
            if (vehiclePlatesIn.contains(plate)) return null;
            if (!isDenied) vehiclePlatesIn.add(plate);
            return plate;
        }
        return null;
    }

    private String pickRandomFromInVehiclePlates() {
        if (!vehiclePlatesIn.isEmpty()) {
            int randomIndex = generateRandomNumberInRange(0, vehiclePlatesIn.size() - 1);
            String plate = vehiclePlatesIn.get(randomIndex);
            vehiclePlatesIn.remove(plate);
            return plate;
        }
        return null;
    }

    private void putDataInInflux(VehicleGateSystemCommand command, VehicleGateMode mode, String triggeredBy, LocalDateTime timestamp) {
        Instant date = timestamp.atZone(ZoneId.of("GMT+1")).toInstant();
        HashMap<String, String> tags = new HashMap<>();
        tags.put(TRIGGERED_BY_TAG, triggeredBy);
        tags.put(MODE_TAG, mode.toString());
        try {
            influxDBQueryService.save(vehicleGate.getId(), VEHICLE_GATE_DEVICE_NAME, VEHICLE_GATE_ACTION, command.toString(), date, tags);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void putHeartbeatInInflux(LocalDateTime timestamp) {
        Instant date = timestamp.atZone(ZoneId.systemDefault()).toInstant();
        influxDBQueryService.save(vehicleGate.getId(), HEARTBEAT_DEVICE_NAME, HEARTBEAT_FIELD, 0.0, date);
    }

    private boolean checkIfFailed() {
        int rand = generateRandomNumberInRange(0, 100000);
        return rand < 10;
    }

    private int generateRandomNumberInRange(int minValue, int maxValue) {
        Random random = new Random();
        return random.nextInt((maxValue - minValue) + 1) + minValue;
    }

    private void generateNotAllowedVehiclePlates() {
        for (int i = 0; i < 15; i++) notAllowedVehiclePlates.add(generatePlate());
    }

    private String generatePlate() {
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
