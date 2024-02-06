package rs.ac.uns.ftn.nwt.simulator_server.utils.ambient_sensor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AmbientSensorUtils {

    public static String formatWithLeadingZero(int number) {
        if (number >= 0 && number <= 9) {
            return "0" + number;
        } else {
            return Integer.toString(number);
        }
    }

    @SneakyThrows
    public static List<AmbientSensorDataEntry> readCsvDataInDateRange(String filePath, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<AmbientSensorDataEntry> dataEntries = new ArrayList<>();

        Stream<String> stream = Files.lines(Paths.get(filePath));
        AtomicBoolean shouldBreak = new AtomicBoolean(false); // Za kontrolu prekida
        stream
                .skip(1)
                .parallel()
                .takeWhile(s -> !shouldBreak.get())
                .forEach(line -> processLine(startDateTime, endDateTime, line, dataEntries, shouldBreak));
        return dataEntries;
    }

    private static void processLine(LocalDateTime startDateTime, LocalDateTime endDateTime, String line,
                                    List<AmbientSensorDataEntry> dataEntries, AtomicBoolean shouldBreak) {
        String[] parts = line.split(",");
        AmbientSensorDataEntry entry = AmbientSensorUtils.parseDataEntry(parts);

        if (isWithinDateInterval(entry.getDateTime(), startDateTime, endDateTime)) dataEntries.add(entry);
        else if (entry.getDateTime().isAfter(endDateTime)) shouldBreak.set(true);
    }

    private static AmbientSensorDataEntry parseDataEntry(String[] parts) {
        String dateStr = parts[0].trim();
        String tempStr = parts[1].trim();
        String humidityStr = parts[2].trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);

        double temperature = Double.parseDouble(tempStr);
        double humidity = Double.parseDouble(humidityStr);

        return new AmbientSensorDataEntry(dateTime, temperature, humidity);
    }

    public static boolean isWithinDateInterval(LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}