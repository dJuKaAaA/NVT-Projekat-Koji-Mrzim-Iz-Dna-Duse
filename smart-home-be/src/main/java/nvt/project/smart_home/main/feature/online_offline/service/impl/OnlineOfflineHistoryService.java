package nvt.project.smart_home.main.feature.online_offline.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.StartTimeIsAfterEndTimeException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampValueHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.online_offline.service.interf.IOnlineOfflineHistoryService;
import nvt.project.smart_home.main.feature.online_offline.web_dto.request.OnlineOfflineWebRequest;
import nvt.project.smart_home.main.feature.online_offline.web_dto.response.OnlineOfflineWebResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.HEARTBEAT_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.HEARTBEAT_FIELD;

@RequiredArgsConstructor
@Service
public class OnlineOfflineHistoryService implements IOnlineOfflineHistoryService {

    private final InfluxDBQueryService influxDBQueryService;
    private final int MILLIS = 100;
    private int OFFSET;
    private int OFFSET_MINI = 60;

    @Override
    public List<OnlineOfflineWebResponse> getGraphData(long deviceId, OnlineOfflineWebRequest request) {
        LocalDateTime now = LocalDateTime.now();
        var historyPeriod = request.getPeriod();

        List<OnlineOfflineWebResponse> history = new ArrayList<>();

        switch (historyPeriod) {
            case H1 -> history = getGraphDataForCustomDate(deviceId, now.minusHours(1), now);
            case H6 -> history = getGraphDataForCustomDate(deviceId, now.minusHours(6), now);
            case H12 -> history = getGraphDataForCustomDate(deviceId, now.minusHours(12), now);
            case H24 -> history = getGraphDataForCustomDate(deviceId, now.minusHours(24), now);
            case W1 -> history = getGraphDataForCustomDate(deviceId, now.minusDays(7), now);
            case M1 -> history = getGraphDataForCustomDate(deviceId, now.minusDays(30), now);
            case CUSTOM_DATE ->  history = getGraphDataForCustomDate(deviceId, request.getStartDateTime(), request.getEndDateTime());
        }
        return history;
    }

    private List<OnlineOfflineWebResponse> getGraphDataForCustomDate(Long deviceId, LocalDateTime startDate, LocalDateTime endDate) {
        checkDate(startDate, endDate);
        Instant endInstant = endDate.atZone(ZoneId.systemDefault()).toInstant();

        HashMap<String, String> tags = new HashMap<>();
        tags.put("id", deviceId.toString());


        List<OnlineOfflineWebResponse> responses = new ArrayList<>();
        String aggregationParam = getAggregationParam(startDate, endDate);
        List<FluxResultDto<Double>> data;

        if (aggregationParam != null) {

            LocalDateTime end = endDate.minusMinutes(5);
            aggregationParam = getAggregationParam(startDate, end);
            data = influxDBQueryService.aggregateAndGet(startDate, end, HEARTBEAT_DEVICE_NAME, HEARTBEAT_FIELD, deviceId, tags, false, "mean", aggregationParam);

            // in past
            Instant start = startDate.atZone(ZoneId.systemDefault()).toInstant();
            if (Duration.between(start, data.getFirst().getTimestamp()).toSeconds() > OFFSET) {
                responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(start.toString()).build());
                Instant date = data.getFirst().getTimestamp().minusMillis(MILLIS);
                responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data.getFirst().getTimestamp().toString()).build());
            } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data.getFirst().getTimestamp().toString()).build());

            for (int i = 1; i < data.size() - 2; i++) {
                FluxResultDto<Double> item1 = data.get(i);
                FluxResultDto<Double> item2 = data.get(i+1);
                if (Duration.between(item1.getTimestamp(), item2.getTimestamp()).getSeconds() > OFFSET) {
                    responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(item1.getTimestamp().toString()).build());
                    Instant date = item1.getTimestamp().plusMillis(MILLIS);
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                    date = item2.getTimestamp().minusMillis(MILLIS);
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(item1.getTimestamp().toString()).build());
            }

            List<FluxResultDto<Double>> data2 = influxDBQueryService.getWithTimeStamp(endDate.minusMinutes(5), endDate, HEARTBEAT_DEVICE_NAME, HEARTBEAT_FIELD, tags);

            if (!data2.isEmpty()) {

                if (Duration.between(data.getLast().getTimestamp(), data2.getFirst().getTimestamp()).toSeconds() > OFFSET_MINI) {
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(start.toString()).build());
                    Instant date = data2.getFirst().getTimestamp().minusMillis(MILLIS);
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                    responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data2.getFirst().getTimestamp().toString()).build());
                } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data2.getFirst().getTimestamp().toString()).build());

                for (int i = 1; i < data2.size() - 2; i++) {
                    FluxResultDto<Double> item1 = data2.get(i);
                    FluxResultDto<Double> item2 = data2.get(i+1);
                    if (Duration.between(item1.getTimestamp(), item2.getTimestamp()).getSeconds() > OFFSET_MINI) {
                        responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(item1.getTimestamp().toString()).build());
                        Instant date = item1.getTimestamp().plusMillis(MILLIS);
                        responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                        date = item2.getTimestamp().minusMillis(MILLIS);
                        responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                    } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(item1.getTimestamp().toString()).build());
                }

                if (Duration.between(data2.getLast().getTimestamp(), endInstant).getSeconds() > OFFSET_MINI) {
                    Instant date = data2.getLast().getTimestamp().plusMillis(MILLIS);
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(endInstant.toString()).build());
                } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data2.getLast().getTimestamp().toString()).build());
            } else {
                if (Duration.between(data.getLast().getTimestamp(), endInstant).getSeconds() > OFFSET_MINI) {
                    Instant date = data.getLast().getTimestamp().plusMillis(MILLIS);
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(endInstant.toString()).build());
                } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data.getLast().getTimestamp().toString()).build());
            }
        }
        else {
            data = influxDBQueryService.getWithTimeStamp(startDate, endDate, HEARTBEAT_DEVICE_NAME, HEARTBEAT_FIELD, tags);
            Instant start = startDate.atZone(ZoneId.systemDefault()).toInstant();
            if (Duration.between(start, data.getFirst().getTimestamp()).toSeconds() > OFFSET_MINI) {
                responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(start.toString()).build());
                Instant date = data.getFirst().getTimestamp().minusMillis(MILLIS);
                responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data.getFirst().getTimestamp().toString()).build());
            } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data.getFirst().getTimestamp().toString()).build());

            for (int i = 1; i < data.size() - 1; i++) {
                FluxResultDto<Double> item1 = data.get(i);
                FluxResultDto<Double> item2 = data.get(i+1);
                if (Duration.between(item1.getTimestamp(), item2.getTimestamp()).getSeconds() > OFFSET_MINI) {
                    responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(item1.getTimestamp().toString()).build());
                    Instant date = item1.getTimestamp().plusMillis(MILLIS);
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                    date = item2.getTimestamp().minusMillis(MILLIS);
                    responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(item1.getTimestamp().toString()).build());
            }

            if (Duration.between(data.getLast().getTimestamp(), endInstant).getSeconds() > OFFSET_MINI) {
                Instant date = data.getLast().getTimestamp().plusMillis(MILLIS);
                responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(date.toString()).build());
                responses.add(OnlineOfflineWebResponse.builder().failed(true).timestamp(endInstant.toString()).build());
            } else responses.add(OnlineOfflineWebResponse.builder().failed(false).timestamp(data.getLast().getTimestamp().toString()).build());
        }
        return responses;
    }

    public String getAggregationParam(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        if (duration.toDays() <= 3) {
            OFFSET = OFFSET_MINI;
            return null;
        }
        if (duration.toDays() <= 10) {
            OFFSET = 60 * 5;
            return "5m";
        }
        else if (duration.toDays() < 20) {
            OFFSET = 60 * 10;
            return "10m";
        }
        else if (duration.toDays() <= 31){
            OFFSET = 60 * 15;
            return "15m";
        }
        else return null;
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
