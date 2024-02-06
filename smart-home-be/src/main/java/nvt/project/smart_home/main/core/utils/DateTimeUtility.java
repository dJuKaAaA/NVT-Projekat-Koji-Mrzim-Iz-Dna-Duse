package nvt.project.smart_home.main.core.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtility {

    public static boolean isTimeInInterval(LocalTime timeToCheck, LocalTime startTime, LocalTime endTime) {
        return !timeToCheck.isBefore(startTime) && !timeToCheck.isAfter(endTime);
    }

    public static LocalDateTime convertToLocalDateTime (Instant dateToConvert){
        return dateToConvert
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static long convertToUnixTimestamp(LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
        return zonedDateTime.toEpochSecond();
    }


    public static boolean isStartDateTimeAfterEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.isAfter(endDate);
    }

    public static boolean isStartDateAfterEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate.isAfter(endDate);
    }

    public static boolean isStartTimeAfterEndDate(LocalTime startDate, LocalTime endDate) {
        return startDate.isAfter(endDate);
    }
}
