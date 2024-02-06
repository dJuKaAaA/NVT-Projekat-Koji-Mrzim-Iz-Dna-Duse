package nvt.project.smart_home.main.feature.device.ambient_sensor.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.StartTimeIsAfterEndTimeException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.DoubleFluxResult;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf.IAmbientSensorHistoryService;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.request.AmbientSensorHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response.AmbientSensorHistoryWebResponseDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.AMBIENT_SENSOR_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.AMBIENT_SENSOR_FIELD_HUMIDITY;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.AMBIENT_SENSOR_FIELD_TEMPERATURE;

@RequiredArgsConstructor
@Service
public class AmbientSensorHistoryService implements IAmbientSensorHistoryService {

    private final InfluxDBQueryService influxDBQueryService;

    @Override
    public List<AmbientSensorHistoryWebResponseDto> getValues(long deviceId, AmbientSensorHistoryWebRequestDto requestDto) {

        LocalDateTime now = LocalDateTime.now();
        var historyPeriod = requestDto.getPeriod();

        return switch (historyPeriod) {
            case H1 -> getValuesForCustomDate(deviceId, now.minusHours(1), now);
            case H6 -> getValuesForCustomDate(deviceId, now.minusHours(6), now);
            case H12 -> getValuesForCustomDate(deviceId, now.minusHours(12), now);
            case H24 -> getValuesForCustomDate(deviceId, now.minusHours(24), now);
            case W1 -> getValuesForCustomDate(deviceId, now.minusDays(7), now);
            case M1 -> getValuesForCustomDate(deviceId, now.minusDays(30), now);
            case CUSTOM_DATE ->  getValuesForCustomDate(deviceId, requestDto.getStartDateTime(), requestDto.getEndDateTime());
        };
    }

    private List<AmbientSensorHistoryWebResponseDto> getValuesForCustomDate(long deviceId, LocalDateTime startDate, LocalDateTime endDate) {


        if(startDate == null || endDate == null)
            throw new BadRequestException("Start date and end date must be provided!");

        if (DateTimeUtility.isStartDateTimeAfterEndDate(startDate, endDate))
            throw new StartTimeIsAfterEndTimeException();

        if(isDurationGreaterThan30Days(startDate, endDate))
            throw new BadRequestException("The difference between dates is greater than one month.");

        var result = influxDBQueryService.getAmbientSensorMeasurements(startDate, endDate, AMBIENT_SENSOR_DEVICE_NAME,
                deviceId);
        // Zadržavanje svakog trećeg elementa
        return IntStream.range(0, result.size())
                .parallel()
                .filter(i -> (i + 1) % 3 == 0)
                .mapToObj(result::get)
                .toList();
    }



    private boolean isDurationGreaterThan30Days(LocalDateTime startDate, LocalDateTime endDate) {
        return getDurationPeriodInDays(startDate, endDate) >= 32;
    }

    private int getDurationPeriodInDays(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }


}
