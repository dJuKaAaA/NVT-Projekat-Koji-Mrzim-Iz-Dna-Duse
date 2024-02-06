package nvt.project.smart_home.main.feature.device.lamp.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.StartTimeIsAfterEndTimeException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultWithTagsDto;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampCommand;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampMode;
import nvt.project.smart_home.main.feature.device.lamp.constants.LampValueType;
import nvt.project.smart_home.main.feature.device.lamp.service.interf.ILampHistoryService;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampActionHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampCommandHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampValueHistoryWebResponseDto;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.LAMP_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.LAMP_VALUES_FIELD;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.*;

@Service
@RequiredArgsConstructor
public class LampHistoryService implements ILampHistoryService {

    private final InfluxDBQueryService influxDBQueryService;

    @Override
    public List<LampValueHistoryWebResponseDto> getValuesHistory(long deviceId, LampHistoryWebRequestDto requestDto, LampValueType valueType) {
        LocalDateTime now = LocalDateTime.now();
        var historyPeriod = requestDto.getPeriod();

        List<LampValueHistoryWebResponseDto> history = new ArrayList<>();

        switch (historyPeriod) {
            case H1 -> history = getValuesForCustomDate(deviceId, now.minusHours(1), now, valueType);
            case H6 -> history = getValuesForCustomDate(deviceId, now.minusHours(6), now, valueType);
            case H12 -> history = getValuesForCustomDate(deviceId, now.minusHours(12), now, valueType);
            case H24 -> history = getValuesForCustomDate(deviceId, now.minusHours(24), now, valueType);
            case W1 -> history = getValuesForCustomDate(deviceId, now.minusDays(7), now, valueType);
            case M1 -> history = getValuesForCustomDate(deviceId, now.minusDays(30), now, valueType);
            case CUSTOM_DATE ->  history = getValuesForCustomDate(deviceId, requestDto.getStartDateTime(), requestDto.getEndDateTime(), valueType);
        }
        return history;
    }

    private List<LampValueHistoryWebResponseDto> getValuesForCustomDate(long deviceId, LocalDateTime startDate, LocalDateTime endDate, LampValueType valueType) {
        checkDate(startDate, endDate);


        if (valueType != LampValueType.ILLUMINATION) return getBulbOn(deviceId, startDate, endDate);
        else return getIllumination(deviceId, startDate, endDate);
    }

    private List<LampValueHistoryWebResponseDto> getIllumination(long deviceId, LocalDateTime startDate, LocalDateTime endDate) {
        HashMap<String, String> tags = new HashMap<>();
        tags.put(LAMP_VALUE_TAG, LampValueType.ILLUMINATION.toString());

        String aggregationParam = getAggregationParam(startDate, endDate);
        List<FluxResultDto<Double>> values;
        if (aggregationParam != null) values = influxDBQueryService.aggregateAndGet(startDate, endDate, LAMP_DEVICE_NAME, LAMP_VALUES_FIELD, deviceId, tags, false, "mean", aggregationParam);
        else values = influxDBQueryService.getByIdAndTagsWithTimeStamp(startDate, endDate, LAMP_DEVICE_NAME, deviceId, tags, false);

        return values.stream().map(
                value -> LampValueHistoryWebResponseDto.builder()
                        .value(value.getValue())
                        .timestamp(value.getTimestamp().toString())
                        .build()
        ).toList();
    }

    private List<LampValueHistoryWebResponseDto> getBulbOn(long deviceId, LocalDateTime startDate, LocalDateTime endDate) {
        List<FluxResultWithTagsDto<String>> actions = influxDBQueryService.getByIdAndTagsWithTimeStampAndTags(startDate, endDate, LAMP_DEVICE_NAME, deviceId, new HashMap<>(), false);
        actions = actions.stream().filter(action -> action.getTags().get(TRIGGERED_BY_TAG) != null
                && !action.getValue().equals(LampCommand.AUTO_MODE_OFF.toString())
                && !action.getValue().equals(LampCommand.AUTO_MODE_ON.toString()))
                .sorted(Comparator.comparing(FluxResultWithTagsDto::getTimestamp))
                .toList();

        List<LampValueHistoryWebResponseDto> result = new ArrayList<>();
        double bulbOn;

        if (actions.isEmpty()) return null;
        else if (actions.size() == 1) {
            bulbOn = 0.0;
            if (actions.getFirst().getValue().equals(LampCommand.ON_BULB.toString())) bulbOn = 1.0;
            result.add(LampValueHistoryWebResponseDto.builder()
                    .value(-(bulbOn - 1.0))
                    .timestamp(startDate.toString())
                    .build());

            result.add(LampValueHistoryWebResponseDto.builder()
                    .value(-(bulbOn - 1.0))
                    .timestamp(actions.getFirst().getTimestamp().minusMillis(100).toString())
                    .build());

            result.add(LampValueHistoryWebResponseDto.builder()
                    .value(bulbOn)
                    .timestamp(actions.getFirst().getTimestamp().toString())
                    .build());

            result.add(LampValueHistoryWebResponseDto.builder()
                    .value(bulbOn)
                    .timestamp(endDate.toString())
                    .build());
            return result;
        } else if (actions.size() == 2) {
            bulbOn = 0.0;
            if (actions.getFirst().getValue().equals(LampCommand.ON_BULB.toString())) bulbOn = 1.0;

            result.add(LampValueHistoryWebResponseDto.builder()
                    .value(-(bulbOn - 1.0))
                    .timestamp(startDate.toString())
                    .build());

            result.add(LampValueHistoryWebResponseDto.builder()
                    .value(-(bulbOn - 1.0))
                    .timestamp(actions.getFirst().getTimestamp().minusMillis(100).toString())
                    .build());

            result.add(LampValueHistoryWebResponseDto.builder()
                    .value(bulbOn)
                    .timestamp(actions.getFirst().getTimestamp().toString())
                    .build());

            bulbOn = 0.0;
            if (actions.getLast().getValue().equals(LampCommand.ON_BULB.toString())) bulbOn = 1.0;
            if (bulbOn == 0.0) {
                result.add(LampValueHistoryWebResponseDto.builder().value(1.0).timestamp(actions.getLast().getTimestamp().minusMillis(100).toString()).build());
                result.add(LampValueHistoryWebResponseDto.builder().value(0.0).timestamp(actions.getLast().getTimestamp().toString()).build());
                result.add(LampValueHistoryWebResponseDto.builder().value(0.0).timestamp(endDate.toString()).build());
            } else {
                result.add(LampValueHistoryWebResponseDto.builder().value(0.0).timestamp(actions.getLast().getTimestamp().minusMillis(100).toString()).build());
                result.add(LampValueHistoryWebResponseDto.builder().value(1.0).timestamp(actions.getLast().getTimestamp().toString()).build());
                result.add(LampValueHistoryWebResponseDto.builder().value(1.0).timestamp(endDate.toString()).build());
            }

            return result;
        }

        bulbOn = 0.0;
        if (actions.getFirst().getValue().equals(LampCommand.ON_BULB.toString()) || actions.getFirst().getValue().equals(LampCommand.AUTO_MODE_ON.toString())) bulbOn = 1.0;

        result.add(LampValueHistoryWebResponseDto.builder()
                .value(-(bulbOn - 1.0))
                .timestamp(startDate.toString())
                .build());

        result.add(LampValueHistoryWebResponseDto.builder()
                .value(-(bulbOn - 1.0))
                .timestamp(actions.getFirst().getTimestamp().minusMillis(100).toString())
                .build());

        result.add(LampValueHistoryWebResponseDto.builder()
                .value(bulbOn)
                .timestamp(actions.getFirst().getTimestamp().toString())
                .build());

        for (int i = 1; i < actions.size(); i++) {
            bulbOn = 0.0;
            if (actions.get(i).getValue().equals(LampCommand.ON_BULB.toString())) bulbOn = 1.0;
            if (bulbOn == 0.0) {
                result.add(LampValueHistoryWebResponseDto.builder().value(1.0).timestamp(actions.get(i).getTimestamp().minusMillis(100).toString()).build());
                result.add(LampValueHistoryWebResponseDto.builder().value(0.0).timestamp(actions.get(i).getTimestamp().toString()).build());
            } else {
                result.add(LampValueHistoryWebResponseDto.builder().value(0.0).timestamp(actions.get(i).getTimestamp().minusMillis(100).toString()).build());
                result.add(LampValueHistoryWebResponseDto.builder().value(1.0).timestamp(actions.get(i).getTimestamp().toString()).build());
            }
        }

        bulbOn = 0.0;
        if (actions.getLast().getValue().equals(LampCommand.ON_BULB.toString()) || actions.getLast().getValue().equals(LampCommand.AUTO_MODE_ON.toString())) bulbOn = 1.0;
        if (bulbOn == 0.0) result.add(LampValueHistoryWebResponseDto.builder().value(0.0).timestamp(endDate.toString()).build());
        else result.add(LampValueHistoryWebResponseDto.builder().value(1.0).timestamp(endDate.toString()).build());

        return result;
    }

    public static String getAggregationParam(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        if (duration.toHours() <= 1) return null;
        if (duration.toHours() <= 12) return "3m";
        else if (duration.toDays() < 5)  return "7m";
        else if (duration.toDays() < 10) return "15m";
        else if (duration.toDays() < 15) return "20m";
        else if (duration.toDays() < 20) return "25m";
        else if (duration.toDays() < 31) return "30m";
        else return null;
    }

    @Override
    public List<LampCommandHistoryWebResponseDto> getCommandHistory(long deviceId, LampActionHistoryWebRequestDto request) {
        LocalDateTime now = LocalDateTime.now();
        var historyPeriod = request.getPeriod();

        List<LampCommandHistoryWebResponseDto> history = new ArrayList<>();

        switch (historyPeriod) {
            case H1 -> history = getCommandsForCustomDate(deviceId, now.minusHours(1), now, request.getTriggeredBy(), request.getMode());
            case H6 -> history = getCommandsForCustomDate(deviceId, now.minusHours(6), now, request.getTriggeredBy(), request.getMode());
            case H12 -> history = getCommandsForCustomDate(deviceId, now.minusHours(12), now, request.getTriggeredBy(), request.getMode());
            case H24 -> history = getCommandsForCustomDate(deviceId, now.minusHours(24), now, request.getTriggeredBy(), request.getMode());
            case W1 -> history = getCommandsForCustomDate(deviceId, now.minusDays(7), now, request.getTriggeredBy(), request.getMode());
            case M1 -> history = getCommandsForCustomDate(deviceId, now.minusDays(30), now, request.getTriggeredBy(), request.getMode());
            case CUSTOM_DATE ->  history = getCommandsForCustomDate(deviceId, request.getStartDateTime(), request.getEndDateTime(), request.getTriggeredBy(), request.getMode());
        }
        return history;
    }

    private List<LampCommandHistoryWebResponseDto> getCommandsForCustomDate(long deviceId, LocalDateTime startDate, LocalDateTime endDate, String triggeredBy, String mode) {
        checkDate(startDate, endDate);

        HashMap<String, String> tags = new HashMap<>();
        if (triggeredBy != null) tags.put(TRIGGERED_BY_TAG, triggeredBy);
        if (mode != null) tags.put(MODE_TAG, mode);
        List<FluxResultWithTagsDto<String>> actions = influxDBQueryService.getByIdAndTagsWithTimeStampAndTags(startDate, endDate, LAMP_DEVICE_NAME, deviceId, tags, false);
        return actions.stream()
                .filter(action -> action.getTags().get(TRIGGERED_BY_TAG) != null && !action.getTags().get(MODE_TAG).equals(LampMode.ERROR.toString()))
                .map(action -> LampCommandHistoryWebResponseDto.builder()
                        .command(action.getValue())
                        .triggeredBy(action.getTags().get(TRIGGERED_BY_TAG))
                        .mode(action.getTags().get(MODE_TAG))
                        .timestamp(action.getTimestamp().toString())
                        .build())
                .toList();
    }

    private void checkDate(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate == null || endDate == null) throw new BadRequestException("Start date and end date must be provided!");
        if (DateTimeUtility.isStartDateTimeAfterEndDate(startDate, endDate)) throw new StartTimeIsAfterEndTimeException();
        if(isDurationGreaterThan30Days(startDate, endDate)) throw new BadRequestException("The difference between dates is greater than one month.");
    }

    private boolean isDurationGreaterThan30Days(LocalDateTime startDate, LocalDateTime endDate) {
        return getDurationPeriodInDays(startDate, endDate) >= 32;
    }

    private int getDurationPeriodInDays(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) throw new IllegalArgumentException("Start date and end date cannot be null");
        return (int)ChronoUnit.DAYS.between(startDate, endDate);
    }
}