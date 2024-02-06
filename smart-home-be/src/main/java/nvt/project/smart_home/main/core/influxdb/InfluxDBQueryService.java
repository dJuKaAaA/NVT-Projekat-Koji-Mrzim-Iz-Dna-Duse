package nvt.project.smart_home.main.core.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.influxdb.fluxResult.DoubleFluxResult;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultWithTagsDto;
import nvt.project.smart_home.main.core.influxdb.fluxResult.StringFluxResult;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response.AmbientSensorHistoryWebResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.AMBIENT_SENSOR_FIELD_HUMIDITY;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.AMBIENT_SENSOR_FIELD_TEMPERATURE;

@RequiredArgsConstructor
@Service
public class InfluxDBQueryService {

    private final InfluxDBClient influxDbClient;
    @Value("${influxdb.bucket}")
    private final String bucket;

    private List<Object> query(String fluxQuery) {
        List<Object> result = new ArrayList<>();
        QueryApi queryApi = this.influxDbClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);
        for (FluxTable fluxTable : tables) {
            System.out.println(fluxTable);
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getValue());
                result.add(fluxRecord.getValue());
            }
        }
        return result;
    }

    private List<DoubleFluxResult> queryDouble(String fluxQuery) {
        QueryApi queryApi = this.influxDbClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);

        return tables.parallelStream()
                .flatMap(fluxTable -> fluxTable.getRecords().stream())
                .map(fluxRecord -> new DoubleFluxResult(
                        fluxRecord.getValue() == null ? 0 : ((double) fluxRecord.getValue()),
                        fluxRecord.getTime()))
                .toList();
    }

    private List<StringFluxResult> queryString(String fluxQuery) {
        List<StringFluxResult> result = new ArrayList<>();
        QueryApi queryApi = this.influxDbClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                result.add(new StringFluxResult(
                        fluxRecord.getValue() == null ? "" : ((String)fluxRecord.getValue()),
                        fluxRecord.getTime() == null ? null : fluxRecord.getTime()));
            }
        }
        return result;
    }

    private <T> List<FluxResultDto<T>> queryGeneric(String fluxQuery) {
        List<FluxResultDto<T>> result = new ArrayList<>();
        QueryApi queryApi = this.influxDbClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                fluxRecord.getMeasurement();
                result.add(new FluxResultDto<T>(
                        fluxRecord.getValue() == null ? null : ((T) fluxRecord.getValue()),
                        fluxRecord.getTime() == null ? null : fluxRecord.getTime()));
            }
        }
        return result;
    }

    // TODO upgrade
    private <T> List<FluxResultWithTagsDto<T>> queryGenericWithTags(String fluxQuery) {
        List<FluxResultWithTagsDto<T>> result = new ArrayList<>();
        QueryApi queryApi = this.influxDbClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                Map<String, Object> values = fluxRecord.getValues();
                Map<String, String> tags = new HashMap<>();
                for (Map.Entry<String, Object> value : values.entrySet()) {
                    if (!value.getKey().equals("result") && !value.getKey().equals("table") && !value.getKey().startsWith("_"))
                        tags.put(value.getKey(), value.getValue().toString());
                }
                result.add(new FluxResultWithTagsDto<T>(
                        fluxRecord.getValue() == null ? null : ((T) fluxRecord.getValue()),
                        tags,   // tags
                        fluxRecord.getTime() == null ? null : fluxRecord.getTime()));
            }
        }
        return result;
    }

    // minutesInPast govori koliko daleko u proslost da gleda sto se tice uzimanja mjera u minutima
    public List<Object> getAll(double minutesInPast, String device, String field) {
        String fluxQuery = """
                from(bucket: "%s")
                |> range(start: -%sm, stop: now())
                |> filter(fn: (r) => r["_measurement"] == "%s")
                |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, minutesInPast, device, field);
        return this.query(fluxQuery);
    }

    public List<Object> get(double timestampInMinutes, String device, String field, Map<String, String> tags) {
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                from(bucket: "%s")
                |> range(start: -%sm, stop: now())
                |> filter(fn: (r) => r["_measurement"] == "%s")
                |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, timestampInMinutes, device, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.query(fluxQuery.toString());
    }

    public List<Object> get(Date startDate, Date endDate, String device, String field, Map<String, String> tags) {
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                from(bucket: "%s")
                |> range(start: %s, stop: %s)
                |> filter(fn: (r) => r["_measurement"] == "%s")
                |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, startDate, endDate, device, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.query(fluxQuery.toString());
    }

    public List<Object> getById(int minutesInPast, String device, String field, long id) {
        String fluxQuery = """
                   from(bucket: "%s")
                       |> range(start: -%sm)
                       |> filter(fn: (r) => r["_measurement"] == "%s")
                       |> filter(fn: (r) => r["_field"] == "%s")
                       |> filter(fn: (r) => r["id"] == "%s")
                """.formatted(bucket, minutesInPast, device, field, id);

        return this.query(fluxQuery);
    }

    public List<Object> getById(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, String field, long id) {
        var startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));
        String fluxQuery = """
                   from(bucket: "%s")
                       |> range(start: %s, stop: %s)
                       |> filter(fn: (r) => r["_measurement"] == "%s")
                       |> filter(fn: (r) => r["_field"] == "%s")
                       |> filter(fn: (r) => r["id"] == "%s")
                """.formatted(bucket, startDateTimeString, endDateTimeString, device, field, id);

        return this.query(fluxQuery);
    }

    public List<DoubleFluxResult> getByIdWithTimeStamp(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, String field, long id) {
        return getByIdWithTimeStamp(startDateTime, endDateTime, device, field, id, new HashMap<>());
    }

    public List<DoubleFluxResult> getByIdWithTimeStamp(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, String field, long id, Map<String, String> tags) {
        var startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                   from(bucket: "%s")
                       |> range(start: %s, stop: %s)
                       |> filter(fn: (r) => r["_measurement"] == "%s")
                       |> filter(fn: (r) => r["_field"] == "%s")
                       |> filter(fn: (r) => r["id"] == "%s")
                """.formatted(bucket, startDateTimeString, endDateTimeString, device, field, id));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.queryDouble(fluxQuery.toString());
    }

    public List<AmbientSensorHistoryWebResponseDto> getAmbientSensorMeasurements(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, long id) {
        String startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));

        String fluxQuery = String.format("""
                       from(bucket: "%s")
                           |> range(start: %s, stop: %s)
                           |> filter(fn: (r) => r["_measurement"] == "%s" and r["id"] == "%s")
                           |> filter(fn: (r) => r["_field"] == "%s" or r["_field"] == "%s")
                           |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
                       """, bucket, startDateTimeString, endDateTimeString, device, id,
                AMBIENT_SENSOR_FIELD_TEMPERATURE, AMBIENT_SENSOR_FIELD_HUMIDITY);

        return this.queryFlux(fluxQuery);
    }

    public List<AmbientSensorHistoryWebResponseDto> queryFlux(String fluxQuery) {

            QueryApi queryApi = influxDbClient.getQueryApi();
            List<FluxTable> tables = queryApi.query(fluxQuery);

            return  tables.stream()
                    .parallel()
                    .flatMap(table -> table.getRecords().stream())
                    .map(record -> {
                        Instant timestamp = record.getTime() != null ? record.getTime() : null;
                        Double temperature = record.getValueByKey(AMBIENT_SENSOR_FIELD_TEMPERATURE) != null ? ((Double) record.getValueByKey(AMBIENT_SENSOR_FIELD_TEMPERATURE)) : null;
                        Double humidity = record.getValueByKey(AMBIENT_SENSOR_FIELD_HUMIDITY) != null ? ((Double) record.getValueByKey(AMBIENT_SENSOR_FIELD_HUMIDITY)) : null;
                        return timestamp != null ? new AmbientSensorHistoryWebResponseDto(String.valueOf(timestamp), temperature, humidity) : null;
                    })
                    .filter(Objects::nonNull)
                    .toList();
    }

    public <T> List<FluxResultDto<T>> getWithTimeStamp(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, String field, Map<String, String> tags) {
        var startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                   from(bucket: "%s")
                       |> range(start: %s, stop: %s)
                       |> filter(fn: (r) => r["_measurement"] == "%s")
                       |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, startDateTimeString, endDateTimeString, device, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.queryGeneric(fluxQuery.toString());
    }

    public <T> List<FluxResultDto<T>> getWithTimeStamp(LocalDateTime startDateTime, LocalDateTime endDateTime, String field, Map<String, String> tags) {
        var startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                   from(bucket: "%s")
                       |> range(start: %s, stop: %s)
                       |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, startDateTimeString, endDateTimeString, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.queryGeneric(fluxQuery.toString());
    }

    public <T> List<FluxResultWithTagsDto<T>> getWithTimeStampWithTags(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, String field, Map<String, String> tags) {
        var startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                   from(bucket: "%s")
                       |> range(start: %s, stop: %s)
                       |> filter(fn: (r) => r["_measurement"] == "%s")
                       |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, startDateTimeString, endDateTimeString, device, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.queryGenericWithTags(fluxQuery.toString());
    }

    public <T> List<FluxResultWithTagsDto<T>> getWithTimeStampWithTags(int minutesInPast, String field, Map<String, String> tags) {
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                   from(bucket: "%s")
                       |> range(start: -%sm)
                       |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, minutesInPast, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.queryGenericWithTags(fluxQuery.toString());
    }

    public <T> List<FluxResultDto<T>> getWithTimeStamp(int minutesInPast, String device, String field, Map<String, String> tags) {
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                   from(bucket: "%s")
                       |> range(start: -%sm, stop: now())
                       |> filter(fn: (r) => r["_measurement"] == "%s")
                       |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, minutesInPast, device, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.queryGeneric(fluxQuery.toString());
    }

    public <T> List<FluxResultDto<T>> getWithTimeStamp(int minutesInPast, String field, Map<String, String> tags) {
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
                   from(bucket: "%s")
                       |> range(start: -%sm, stop: now())
                       |> filter(fn: (r) => r["_field"] == "%s")
                """.formatted(bucket, minutesInPast, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }

        return this.queryGeneric(fluxQuery.toString());
    }

    public <T> List<FluxResultDto<T>> aggregateAndGetWithoutMeasurement(int minutesInPast, String field, Map<String, String> tags, boolean desc, String aggregationFunction, String aggregationWindow) {
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
               from(bucket: "%s")
                   |> range(start: -%s, stop: now())
                   |> filter(fn: (r) => r["_field"] == "%s")
            """.formatted(bucket, minutesInPast, field));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }
        fluxQuery.append("""
            |> aggregateWindow(every: %s, fn: %s, createEmpty: false)
            |> sort(columns: ["_time"], desc: %b)
            """.formatted(aggregationWindow, aggregationFunction, desc));
        return this.queryGeneric(fluxQuery.toString());
    }

    public <T> List<FluxResultDto<T>> aggregateAndGet(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, String field, long id, Map<String, String> tags, boolean desc, String aggregationFunction, String aggregationWindow) {
        var startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
               from(bucket: "%s")
                   |> range(start: %s, stop: %s)
                   |> filter(fn: (r) => r["_measurement"] == "%s")
                   |> filter(fn: (r) => r["_field"] == "%s")
                   |> filter(fn: (r) => r["id"] == "%s")
            """.formatted(bucket, startDateTimeString, endDateTimeString, device, field, id));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }
        fluxQuery.append("""
            |> aggregateWindow(every: %s, fn: %s, createEmpty: false)
            |> sort(columns: ["_time"], desc: %b)
            """.formatted(aggregationWindow, aggregationFunction, desc));
        return this.queryGeneric(fluxQuery.toString());
    }

    public <T> List<FluxResultDto<T>> getByIdAndTagsWithTimeStamp(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, long id, Map<String, String> tags, boolean desc) {
        var startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
               from(bucket: "%s")
                   |> range(start: %s, stop: %s)
                   |> filter(fn: (r) => r["_measurement"] == "%s")
                   |> filter(fn: (r) => r["id"] == "%s")
            """.formatted(bucket, startDateTimeString, endDateTimeString, device, id));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }
        fluxQuery.append("|> sort(columns: [\"_time\"], desc: %b)".formatted(desc)); // Pass boolean value for desc
        return this.queryGeneric(fluxQuery.toString());
    }

    public <T> List<FluxResultWithTagsDto<T>> getByIdAndTagsWithTimeStampAndTags(LocalDateTime startDateTime, LocalDateTime endDateTime, String device, long id, Map<String, String> tags, boolean desc) {
        var startDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(startDateTime));
        String endDateTimeString = String.valueOf(DateTimeUtility.convertToUnixTimestamp(endDateTime));
        StringBuilder fluxQuery = new StringBuilder();
        fluxQuery.append("""
               from(bucket: "%s")
                   |> range(start: %s, stop: %s)
                   |> filter(fn: (r) => r["_measurement"] == "%s")
                   |> filter(fn: (r) => r["id"] == "%s")
            """.formatted(bucket, startDateTimeString, endDateTimeString, device, id));
        for (String tag : tags.keySet()) {
            fluxQuery.append("\n|> filter(fn: (r) => r[\"%s\"] == \"%s\")".formatted(tag, tags.get(tag)));
        }
        fluxQuery.append("|> sort(columns: [\"_time\"], desc: %b)".formatted(desc)); // Pass boolean value for desc
        return this.queryGenericWithTags(fluxQuery.toString());
    }

    public void save(long id, String measurementName, String field, int value, Instant timestamp) {
        save(id, measurementName, field, value, timestamp, new HashMap<>());
    }

    public void save(long id, String measurementName, String field, int value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .addTag("id", String.valueOf(id))
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(String measurementName, String field, int value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(long id, String measurementName, String field, long value, Instant timestamp) {
        save(id, measurementName, field, value, timestamp, new HashMap<>());
    }

    public void save(long id, String measurementName, String field, long value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .addTag("id", String.valueOf(id))
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(String measurementName, String field, long value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(long id, String measurementName, String field, float value, Instant timestamp) {
        save(id, measurementName, field, value, timestamp, new HashMap<>());
    }

    public void save(long id, String measurementName, String field, float value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .addTag("id", String.valueOf(id))
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(String measurementName, String field, float value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(long id, String measurementName, String field, double value, Instant timestamp) {
        save(id, measurementName, field, value, timestamp, new HashMap<>());
    }

    public void save(long id, String measurementName, String field, double value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .addTag("id", String.valueOf(id))
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(String measurementName, String field, double value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(long id, String measurementName, String field, boolean value, Instant timestamp) {
        save(id, measurementName, field, value, timestamp, new HashMap<>());
    }

    public void save(long id, String measurementName, String field, boolean value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .addTag("id", String.valueOf(id))
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

    public void save(long id, String measurementName, String field, String value, Instant timestamp) {
        save(id, measurementName, field, value, timestamp, new HashMap<>());
    }

    public void save(long id, String measurementName, String field, String value, Instant timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(measurementName)
                .addField(field, value)
                .addTag("id", String.valueOf(id))
                .time(timestamp, WritePrecision.MS);
        for (String tag : tags.keySet()) {
            point.addTag(tag, tags.get(tag));
        }

        writeApi.writePoint(point);
    }

}
