package nvt.project.smart_home.main.feature.device.sprinkler_system.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.StartTimeIsAfterEndTimeException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultWithTagsDto;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf.ISprinklerSystemHistoryService;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SprinklerSystemHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response.SprinklerSystemHistoryResponseWebDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.SPRINKLER_SYSTEM_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TRIGGERED_BY_TAG;

@RequiredArgsConstructor
@Service
public class SprinklerSystemHistoryService implements ISprinklerSystemHistoryService {
    private final InfluxDBQueryService influxDBQueryService;

    @Override
    public List<SprinklerSystemHistoryResponseWebDto> getHistoryOfActions(long deviceId, SprinklerSystemHistoryWebRequestDto request) {
        LocalDateTime now = LocalDateTime.now();
        var historyPeriod = request.getPeriod();

        List<SprinklerSystemHistoryResponseWebDto> history = new ArrayList<>();

        switch (historyPeriod) {
            case H1 -> history = getHistoryOfActionsForCustomDate(deviceId, now.minusHours(1), now, request.getTriggeredBy());
            case H6 -> history = getHistoryOfActionsForCustomDate(deviceId, now.minusHours(6), now, request.getTriggeredBy());
            case H12 -> history = getHistoryOfActionsForCustomDate(deviceId, now.minusHours(12), now, request.getTriggeredBy());
            case H24 -> history = getHistoryOfActionsForCustomDate(deviceId, now.minusHours(24), now, request.getTriggeredBy());
            case W1 -> history = getHistoryOfActionsForCustomDate(deviceId, now.minusDays(7), now, request.getTriggeredBy());
            case M1 -> history = getHistoryOfActionsForCustomDate(deviceId, now.minusDays(30), now, request.getTriggeredBy());
            case CUSTOM_DATE -> history = getHistoryOfActionsForCustomDate(deviceId, request.getStartDateTime(), request.getEndDateTime(), request.getTriggeredBy());
        }
        return history;
    }

    private List<SprinklerSystemHistoryResponseWebDto> getHistoryOfActionsForCustomDate(long deviceId, LocalDateTime startDate, LocalDateTime endDateTime, String triggeredBy) {
        checkDate(startDate, endDateTime);

        HashMap<String, String> tags = new HashMap<>();
        if (triggeredBy != null) tags.put(TRIGGERED_BY_TAG, triggeredBy);
        List<FluxResultWithTagsDto<String>> actions = influxDBQueryService.getByIdAndTagsWithTimeStampAndTags(startDate,endDateTime, SPRINKLER_SYSTEM_DEVICE_NAME, deviceId, tags, false);

        return actions.stream().map(
                action -> SprinklerSystemHistoryResponseWebDto.builder()
                        .status(action.getValue())
                        .timestamp(action.getTimestamp().toString())
                        .triggeredBy(action.getTags().get(TRIGGERED_BY_TAG))
                        .build()
        ).toList();
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
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }
}
