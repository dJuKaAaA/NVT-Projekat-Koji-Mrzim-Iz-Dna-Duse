package nvt.project.smart_home.main.feature.power_consumption.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.feature.power_consumption.service.interf.IPowerConsumptionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.DEVICE_POWER_CONSUMPTION_FIELD;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TAG_KEY_CITY_ID;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TAG_KEY_PROPERTY_ID;

@RequiredArgsConstructor
@Service
public class PowerConsumptionService implements IPowerConsumptionService {

    private final InfluxDBQueryService influxDBQueryService;

    @Override
    public Collection<FluxResultDto<Double>> getConsumedEnergyForProperty(Long propertyId, int minutesInPast) {
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));

        List<FluxResultDto<Double>> resultList = influxDBQueryService.getWithTimeStamp(
                minutesInPast,
                DEVICE_POWER_CONSUMPTION_FIELD,
                tags);

        long intervalMinutes = (minutesInPast > 60 * 6) ? (minutesInPast / (60 * 6)) : (1);
        return sumUpToIntervalConsumption(resultList, intervalMinutes);
    }

    @Override
    public Collection<FluxResultDto<Double>> getConsumedEnergyForProperty(Long propertyId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));

        List<FluxResultDto<Double>> resultList = influxDBQueryService.getWithTimeStamp(
                startDate,
                endDate,
                DEVICE_POWER_CONSUMPTION_FIELD,
                tags);

        long minutesBetweenDates = ChronoUnit.MINUTES.between(startDate, endDate);
        long intervalMinutes = (minutesBetweenDates > 60 * 6) ? (minutesBetweenDates / (60 * 6)) : (1);
        return sumUpToIntervalConsumption(resultList, intervalMinutes);
    }

    @Override
    public Collection<FluxResultDto<Double>> getConsumedEnergyForCity(Long cityId, int minutesInPast) {
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_CITY_ID, String.valueOf(cityId));

        List<FluxResultDto<Double>> resultList = influxDBQueryService.getWithTimeStamp(
                minutesInPast,
                DEVICE_POWER_CONSUMPTION_FIELD,
                tags);

        long intervalMinutes = (minutesInPast > 60 * 6) ? (minutesInPast / (60 * 6)) : (1);
        return sumUpToIntervalConsumption(resultList, intervalMinutes);
    }

    @Override
    public Collection<FluxResultDto<Double>> getConsumedEnergyForCity(Long cityId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_CITY_ID, String.valueOf(cityId));

        List<FluxResultDto<Double>> resultList = influxDBQueryService.getWithTimeStamp(
                startDate,
                endDate,
                DEVICE_POWER_CONSUMPTION_FIELD,
                tags);

        long minutesBetweenDates = ChronoUnit.MINUTES.between(startDate, endDate);
        long intervalMinutes = (minutesBetweenDates > 60 * 6) ? (minutesBetweenDates / (60 * 6)) : (1);
        return sumUpToIntervalConsumption(resultList, intervalMinutes);
    }

    private List<FluxResultDto<Double>> sumUpToIntervalConsumption(List<FluxResultDto<Double>> resultFromInflux, long intervalMinutes) {
        Map<Long, Double> consumptionPerInterval = resultFromInflux.stream()
                .collect(Collectors.groupingBy(
                        result -> result.getTimestamp().getEpochSecond() / (intervalMinutes * 60L),
                        Collectors.summingDouble(result -> (double) result.getValue())
                ));

        return consumptionPerInterval.entrySet().stream()
                .map(entry -> new FluxResultDto<>(entry.getValue(), Instant.ofEpochSecond(entry.getKey() * intervalMinutes * 60L)))
                .collect(Collectors.toList());
    }


}
