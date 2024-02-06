package nvt.project.smart_home.test_script.sprinkler_system_utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.feature.device.sprinkler_system.SprinklerSystemStatus;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.SPRINKLER_SYSTEM_STATUS_FIELD;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TRIGGERED_BY_TAG;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprinklerSystemInfluxDBDataGenerator {

    private SprinklerSystemEntity sprinklerSystem;
    private InfluxDBQueryService influxDBQueryService;

    private final int NUM_OF_DAYS = 90;
    private final int GENERATE_TIME_SEC = 45;
    private final String TRIGGERED_BY_SYSTEM = "SYSTEM";

    public void generateData() {
        System.out.println("### Start InfluxDB generator for: " + sprinklerSystem.getName());

        LocalTime userStartTime = generateUserTimeStart();
        DayOfWeek userDayOfWeek = generateUserDayOfWeek();
        String userEmail = sprinklerSystem.getProperty().getOwner().getEmail();
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
            if (isGoodDay(startTime.getDayOfWeek())) {
                if (isGoodTime(LocalTime.from(startTime), sprinklerSystem.getSchedule().getFirst().getStartTime())) {
                    putInInflux(TRIGGERED_BY_SYSTEM, SprinklerSystemStatus.ON, startTime);
                } else if (isGoodTime(LocalTime.from(startTime), sprinklerSystem.getSchedule().getFirst().getEndTime())) {
                    putInInflux(TRIGGERED_BY_SYSTEM, SprinklerSystemStatus.OFF, startTime);
                }
            }
            if (startTime.getDayOfWeek() == userDayOfWeek && isGoodTime(LocalTime.from(startTime), userStartTime)) {
                int randomMinute = generateRandomNumberInRange(20, 35);
                LocalDateTime start = startTime.plusMinutes(userStartTime.getMinute() - startTime.getMinute());
                LocalDateTime end = start.plusMinutes(randomMinute);
                putInInflux(userEmail, SprinklerSystemStatus.ON, start);
                putInInflux(userEmail, SprinklerSystemStatus.OFF, end);
            }
            startTime = startTime.plusSeconds(GENERATE_TIME_SEC);
        }
    }

    private boolean checkIfFailed() {
        int rand = generateRandomNumberInRange(0, 100000);
        return rand < 10;
    }

    private boolean isGoodDay(DayOfWeek day) {
        int index = sprinklerSystem.getSchedule().getFirst().getDays().indexOf(day);
        return index != -1;
    }

    private boolean isGoodTime(LocalTime time1, LocalTime time2) {
        return time1.getHour() == time2.getHour() && time1.getMinute() == time2.getMinute() && time1.getSecond() < GENERATE_TIME_SEC;
    }

    private LocalTime generateUserTimeStart() {
        Random random = new Random();
        int randomHour = random.nextInt(15) + 7; // 7 - 21
        return LocalTime.of(randomHour, 0, 0);
    }

    private DayOfWeek generateUserDayOfWeek() {
        DayOfWeek userDayOfWeek = DayOfWeek.of(generateRandomNumberInRange(1, 7));
        while (isGoodDay(userDayOfWeek)) userDayOfWeek = DayOfWeek.of(generateRandomNumberInRange(1, 7));
        return userDayOfWeek;
    }

    private int generateRandomNumberInRange(int minValue, int maxValue) {
        Random random = new Random();
        return random.nextInt((maxValue - minValue) + 1) + minValue;
    }

    private void putInInflux(String triggeredBy, SprinklerSystemStatus status, LocalDateTime timestamp) {
        Instant date = timestamp.atZone(ZoneId.of("GMT+1")).toInstant();
        HashMap<String, String> tags = new HashMap<>();
        tags.put(TRIGGERED_BY_TAG, triggeredBy);
        influxDBQueryService.save(sprinklerSystem.getId(), SPRINKLER_SYSTEM_DEVICE_NAME, SPRINKLER_SYSTEM_STATUS_FIELD, status.toString(), date, tags);
    }

    private void putHeartbeatInInflux(LocalDateTime timestamp) {
        Instant date = timestamp.atZone(ZoneId.systemDefault()).toInstant();
        influxDBQueryService.save(sprinklerSystem.getId(), HEARTBEAT_DEVICE_NAME, HEARTBEAT_FIELD, 0.0, date);
    }

}