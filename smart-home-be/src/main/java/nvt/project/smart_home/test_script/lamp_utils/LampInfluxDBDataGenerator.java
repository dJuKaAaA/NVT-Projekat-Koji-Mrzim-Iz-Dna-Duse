package nvt.project.smart_home.test_script.lamp_utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampCommand;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampMode;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampValueType;
import nvt.project.smart_home.main.feature.device.lamp.entity.LampEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.SprinklerSystemStatus;

import java.time.*;
import java.util.HashMap;
import java.util.stream.IntStream;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.HEARTBEAT_FIELD;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.*;

@Data
@NoArgsConstructor
public class LampInfluxDBDataGenerator {
    private LampEntity lamp;
    private InfluxDBQueryService influxDBQueryService;

    private final int NUM_OF_DAYS = 90;
    private final int GENERATE_TIME_SEC = 45;
    private final String TRIGGERED_BY_SYSTEM = "SYSTEM";

    public void generateData() {
        System.out.println("### Start InfluxDB generator for: " + lamp.getName());
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(NUM_OF_DAYS);


//        int minutesOffset = NUM_OF_DAYS;
//        while (startTime.isBefore(endTime)) {
//            putIlluminationInInflux(getIllumination(startTime, minutesOffset), startTime);
//            if (startTime.getDayOfWeek() != startTime.plusSeconds(GENERATE_TIME_SEC).getDayOfWeek()) minutesOffset -= 2;
//            startTime = startTime.plusSeconds(GENERATE_TIME_SEC);
//        }

//        LocalDateTime finalStartTime = startTime;
        IntStream.range(0, NUM_OF_DAYS + 1)
                .parallel()
                .forEach(i -> {
                    startThread(startTime.plusDays(i), i);
                });
    }

    private void startThread(LocalDateTime startTime, int dayOffset) {
        LocalDateTime endTime = startTime.plusDays(1);
        //System.out.println(startTime);
        boolean isManual = false;
        boolean bulbOn = false;

        int minutesOffset = (NUM_OF_DAYS - dayOffset) / 2;

        // provjeri da li je upaljena vec u drugoj petlji za auto mode
        if (startTime.getDayOfWeek().getValue() <= 5 && getIllumination(startTime, minutesOffset) < 50) bulbOn = true;

        while (startTime.isBefore(endTime)) {

            // offline
            if ((startTime.getDayOfMonth() == 30  && startTime.getMonthValue() == 1) || (startTime.getDayOfMonth() == 19  && startTime.getMonthValue() == 12)) {
                if (startTime.getHour() == 0 && startTime.getMinute() == 0 && startTime.getSecond() < GENERATE_TIME_SEC)
                    putChangeCommandInInflux(LampCommand.OFF_BULB, lamp.getProperty().getOwner().getEmail(), LampMode.ERROR, startTime);
                else if (startTime.getHour() == 23 && startTime.getMinute() == 59 && startTime.getSecond() < GENERATE_TIME_SEC)
                    putChangeCommandInInflux(LampCommand.ON_BULB, lamp.getProperty().getOwner().getEmail(), LampMode.ERROR, startTime);
                startTime = startTime.plusSeconds(GENERATE_TIME_SEC);
                continue;
            }

            // online
            int illumination = getIllumination(startTime, minutesOffset);
            if (startTime.getDayOfWeek().getValue() > 5) {
                //System.out.println(startTime);
                isManual = true;
                if (startTime.getDayOfWeek() == DayOfWeek.SATURDAY && startTime.getHour() == 0 && startTime.getMinute() == 0 && startTime.getSecond() < GENERATE_TIME_SEC) {
                    putChangeCommandInInflux(LampCommand.AUTO_MODE_OFF, lamp.getProperty().getOwner().getEmail(), LampMode.MANUAL_MODE, startTime);
                }
                else if (startTime.getDayOfWeek() == DayOfWeek.SUNDAY && startTime.getHour() == 23 && startTime.getMinute() == 59 && startTime.getSecond() < GENERATE_TIME_SEC) {
                    putChangeCommandInInflux(LampCommand.AUTO_MODE_ON, lamp.getProperty().getOwner().getEmail(), LampMode.AUTO_MODE, startTime);
                }
                else if (startTime.getHour() == 5 && startTime.getMinute() == 0 && startTime.getSecond() < GENERATE_TIME_SEC) {
                    bulbOn = false;
                    putChangeCommandInInflux(LampCommand.OFF_BULB, lamp.getProperty().getOwner().getEmail(), LampMode.MANUAL_MODE, startTime);
                }
                else if (startTime.getHour() == 19 && startTime.getMinute() == 0 && startTime.getSecond() < GENERATE_TIME_SEC){
                    bulbOn = true;
                    putChangeCommandInInflux(LampCommand.ON_BULB, lamp.getProperty().getOwner().getEmail(), LampMode.MANUAL_MODE, startTime);
                }
            } else isManual = false;

            boolean shouldBeOn = illumination < 50;
            if (!isManual && shouldBeOn != bulbOn) {
                bulbOn = !bulbOn;
                putChangeCommandInInflux(bulbOn ? LampCommand.ON_BULB : LampCommand.OFF_BULB, TRIGGERED_BY_SYSTEM, LampMode.AUTO_MODE, startTime);
            }

            putHeartbeatInInflux(startTime);
            putIlluminationInInflux(illumination, startTime);
            startTime = startTime.plusSeconds(GENERATE_TIME_SEC);
        }
    }

    public static int getIllumination(LocalDateTime date, int minuteOffset) {
        int maxIllumination = 100;
        int minIllumination = 0;

        LocalDateTime sunsetStart = date.withHour(16).withMinute(0).withSecond(0); // 100 pocinje da se spusta
        LocalDateTime sunsetDone = date.withHour(20).withMinute(0).withSecond(0).plusMinutes(minuteOffset); // na 0 je do ovdje se spusti
        LocalDateTime sunriseStart = date.withHour(4).withMinute(0).withSecond(0).minusMinutes(minuteOffset); // 0 do ovdje uvijek na 0
        LocalDateTime sunriseDone = date.withHour(8).withMinute(0).withSecond(0); // 100 do ovde se dize

        if (date.isAfter(sunriseDone) && date.isBefore(sunsetStart)) {
            return maxIllumination;
        } else if ((date.isAfter(sunsetStart) || date.isEqual(sunsetStart)) && (date.isBefore(sunsetDone) || date.isEqual(sunsetDone))) {
            double t = (double) (date.toEpochSecond(ZoneOffset.UTC) - sunsetStart.toEpochSecond(ZoneOffset.UTC)) /
                    (sunsetDone.toEpochSecond(ZoneOffset.UTC) - sunsetStart.toEpochSecond(ZoneOffset.UTC));
            double illuminationFactor = quadraticInterpolation(t, -1.0, 0.0, 1.0);
            return (int)(maxIllumination * illuminationFactor );
        } else if (date.isAfter(sunsetDone) && date.isBefore(sunriseStart)) {
            return minIllumination;
        } else if ((date.isAfter(sunriseStart) || date.isEqual(sunriseStart)) && (date.isBefore(sunriseDone) || date.isEqual(sunriseDone))) {
            double t = (double) (date.toEpochSecond(ZoneOffset.UTC) - sunriseStart.toEpochSecond(ZoneOffset.UTC)) /
                    (sunriseDone.toEpochSecond(ZoneOffset.UTC) - sunriseStart.toEpochSecond(ZoneOffset.UTC));
            double illuminationFactor = -quadraticInterpolation(t, 1.0, -2.0, 0.0);
            return (int) (minIllumination + (maxIllumination - minIllumination) * illuminationFactor);
        } else {
            return minIllumination;
        }
    }

    private static double quadraticInterpolation(double t, double a, double b, double c) { return a * t * t + b * t + c;}

    private void putIlluminationInInflux(int illumination, LocalDateTime timestamp) {
        Instant date = timestamp.atZone(ZoneId.of("GMT+1")).toInstant();
        HashMap<String, String> tags = new HashMap<>();
        tags.put(LAMP_VALUE_TAG, LampValueType.ILLUMINATION.toString());
        influxDBQueryService.save(lamp.getId(), LAMP_DEVICE_NAME, LAMP_VALUES_FIELD, (double) illumination, date, tags);
    }

    private void putChangeCommandInInflux(LampCommand action, String triggeredBy, LampMode mode, LocalDateTime timestamp) {
        Instant date = timestamp.atZone(ZoneId.of("GMT+1")).toInstant();
        HashMap<String, String> tags = new HashMap<>();
        tags.put(TRIGGERED_BY_TAG, triggeredBy);
        tags.put(MODE_TAG, mode.toString());
        influxDBQueryService.save(lamp.getId(), LAMP_DEVICE_NAME, LAMP_ACTION_FIELD, action.toString(), date, tags);
    }

    private void putHeartbeatInInflux(LocalDateTime timestamp) {
        Instant date = timestamp.atZone(ZoneId.systemDefault()).toInstant();
        influxDBQueryService.save(lamp.getId(), HEARTBEAT_DEVICE_NAME, HEARTBEAT_FIELD, 1.0, date);
    }

}
