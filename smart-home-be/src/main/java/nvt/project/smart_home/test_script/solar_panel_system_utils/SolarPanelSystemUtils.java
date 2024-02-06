package nvt.project.smart_home.test_script.solar_panel_system_utils;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class SolarPanelSystemUtils {

    public static double calculateProducedEnergy(
            Calendar sunriseCalendar,
            Calendar noonCalendar,
            Calendar targetCalendar,
            Calendar sunsetCalendar,
            double maxEnergyProduced
    ) {
        // Convert to epoch time (seconds since the epoch)
        long sunriseEpoch = sunriseCalendar.getTimeInMillis() / 1000;
        long noonEpoch = noonCalendar.getTimeInMillis() / 1000;
        long targetEpoch = targetCalendar.getTimeInMillis() / 1000;
        long sunsetEpoch = sunsetCalendar.getTimeInMillis() / 1000;

        double percentage = 0.0;
        double noonValue = (noonEpoch - sunriseEpoch) / 60.0;
        double sunsetValue = (sunsetEpoch - sunriseEpoch) / 60.0;
        double value = (targetEpoch - sunriseEpoch) / 60.0;

        if (sunriseEpoch <= targetEpoch && targetEpoch <= sunsetEpoch) {
            List<Double> percentages = new ArrayList<>();
            for (int i = 0; i < (int) sunsetValue; i++) {
                double mappedPercentage = mapToPercentage(i, noonValue, sunsetValue);
                percentages.add(mappedPercentage);
            }

            List<Double> normalizedPercentages = normalizePercentages(percentages);
            percentage = normalizedPercentages.get((int) value);

            return maxEnergyProduced * (percentage / 100);
        } else {
            return 0.0;
        }
    }

    private static double mapToPercentage(int index, double noonValue, double sunsetValue) {
        // Updated implementation to make percentage higher when index is close to noonValue
        double distanceToNoon = Math.abs(index - noonValue);
        return 100 - (distanceToNoon / noonValue) * 100;
    }


    private static List<Double> normalizePercentages(List<Double> percentages) {
        // Placeholder implementation, replace with your normalization logic
        double sum = percentages.stream().mapToDouble(Double::doubleValue).sum();
        return percentages.stream().map(p -> p / sum * 100).toList();
    }

    public static Calendar getCalendar(Calendar base, int hour, int minute, int second) {
        Calendar calendar = (Calendar) base.clone();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

}
