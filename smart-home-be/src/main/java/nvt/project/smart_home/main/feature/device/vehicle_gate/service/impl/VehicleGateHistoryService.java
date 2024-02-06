package nvt.project.smart_home.main.feature.device.vehicle_gate.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.StartTimeIsAfterEndTimeException;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultWithTagsDto;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.vehicle_gate.constants.VehicleGateSystemCommand;
import nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf.IVehicleGateHistoryService;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request.VehicleGateHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateActionsHistoryResponseDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateInOutHistoryWebResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.VEHICLE_GATE_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.MODE_TAG;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.TRIGGERED_BY_TAG;

@RequiredArgsConstructor
@Service
public class VehicleGateHistoryService implements IVehicleGateHistoryService {
    private final InfluxDBQueryService influxDBQueryService;
    @Override
    public List<VehicleGateActionsHistoryResponseDto> getActionsHistory(long deviceId, VehicleGateHistoryWebRequestDto request) {
        LocalDateTime now = LocalDateTime.now();
        var historyPeriod = request.getPeriod();

        List<VehicleGateActionsHistoryResponseDto> history = new ArrayList<>();

        switch (historyPeriod) {
            case H1 -> history = getPlateForCustomDate(deviceId, now.minusHours(1), now, request.getTriggeredBy(), request.getMode());
            case H6 -> history = getPlateForCustomDate(deviceId, now.minusHours(6), now, request.getTriggeredBy(), request.getMode());
            case H12 -> history = getPlateForCustomDate(deviceId, now.minusHours(12), now, request.getTriggeredBy(), request.getMode());
            case H24 -> history = getPlateForCustomDate(deviceId, now.minusHours(24), now, request.getTriggeredBy(), request.getMode());
            case W1 -> history = getPlateForCustomDate(deviceId, now.minusDays(7), now, request.getTriggeredBy(), request.getMode());
            case M1 -> history = getPlateForCustomDate(deviceId, now.minusDays(30), now, request.getTriggeredBy(), request.getMode());
            case CUSTOM_DATE -> {
                history = getPlateForCustomDate(deviceId, request.getStartDateTime(), request.getEndDateTime(), request.getTriggeredBy(), request.getMode());
            }
        }
        return history;
    }

    private List<VehicleGateActionsHistoryResponseDto> getPlateForCustomDate(long deviceId, LocalDateTime startDate, LocalDateTime endDate, String triggeredBy, String mode) {
        checkDate(startDate, endDate);

        HashMap<String, String> tags = new HashMap<>();
        if (triggeredBy != null) tags.put(TRIGGERED_BY_TAG, triggeredBy);
        if (mode != null) tags.put(MODE_TAG, mode);

        List<FluxResultWithTagsDto<String>> actions = influxDBQueryService.getByIdAndTagsWithTimeStampAndTags(startDate, endDate, VEHICLE_GATE_DEVICE_NAME, deviceId, tags, false);

        return actions.stream().map(
                action -> VehicleGateActionsHistoryResponseDto.builder()
                        .command(action.getValue())
                        .triggeredBy(action.getTags().get(TRIGGERED_BY_TAG))
                        .mode(action.getTags().get(MODE_TAG))
                        .timestamp(action.getTimestamp().toString())
                        .build()
        ).toList();
    }

    @Override
    public List<VehicleGateInOutHistoryWebResponseDto> getInOutHistory(long deviceId, VehicleGateHistoryWebRequestDto request) {
        if (request.getTriggeredBy() == null) throw new BadRequestException("Triggered by can not be null.");

        LocalDateTime now = LocalDateTime.now();
        var historyPeriod = request.getPeriod();

        List<VehicleGateInOutHistoryWebResponseDto> history = new ArrayList<>();

        switch (historyPeriod) {
            case H1 -> history = getInOutHistoryForCustomDate(deviceId, now.minusHours(1), now, request.getTriggeredBy());
            case H6 -> history = getInOutHistoryForCustomDate(deviceId, now.minusHours(6), now, request.getTriggeredBy());
            case H12 -> history = getInOutHistoryForCustomDate(deviceId, now.minusHours(12), now, request.getTriggeredBy());
            case H24 -> history = getInOutHistoryForCustomDate(deviceId, now.minusHours(24), now, request.getTriggeredBy());
            case W1 -> history = getInOutHistoryForCustomDate(deviceId, now.minusDays(7), now, request.getTriggeredBy());
            case M1 -> history = getInOutHistoryForCustomDate(deviceId, now.minusDays(30), now, request.getTriggeredBy());
            case CUSTOM_DATE -> history = getInOutHistoryForCustomDate(deviceId, request.getStartDateTime(), request.getEndDateTime(), request.getTriggeredBy());
        }
        return history;
    }

    private List<VehicleGateInOutHistoryWebResponseDto> getInOutHistoryForCustomDate(long deviceId, LocalDateTime startDate, LocalDateTime endDate, String triggeredBy) {
        checkDate(startDate, endDate);

        HashMap<String, String> tags = new HashMap<>();
        tags.put(TRIGGERED_BY_TAG, triggeredBy);
        List<FluxResultWithTagsDto<String>> inOutInfo = influxDBQueryService.getByIdAndTagsWithTimeStampAndTags(startDate, endDate, VEHICLE_GATE_DEVICE_NAME, deviceId, tags, false);

        List<VehicleGateInOutHistoryWebResponseDto> result = new ArrayList<>();

        for (FluxResultWithTagsDto<String> info : inOutInfo) {
            if (!info.getValue().equalsIgnoreCase(VehicleGateSystemCommand.DENIED.toString())) {
                boolean isVehicleIn = info.getValue().equals(VehicleGateSystemCommand.IN.toString());
                String timestamp = info.getTimestamp().toString();

                VehicleGateInOutHistoryWebResponseDto dto = VehicleGateInOutHistoryWebResponseDto.builder()
                        .isVehicleIn(isVehicleIn)
                        .timestamp(timestamp)
                        .build();

                result.add(dto);
            }
        }

        return result;
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
