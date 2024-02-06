package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InfluxDBQueryService {

    private final InfluxDBClient influxDbClient;

    private List<Object> query(String fluxQuery) {
        List<Object> result = new ArrayList<>();
        QueryApi queryApi = this.influxDbClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                result.add(fluxRecord.getValue());
            }
        }
        return result;
    }

    // timestampInMinutes govori koliko daleko u proslost da gleda sto se tice uzimanja mjera u minutima
    public List<Object> getAll(String bucket, double timestampInMinutes, String device, String field) {
        String fluxQuery = """
                from(bucket: "%s")
                |> range(start: -%fm, stop: now())
                |> filter(fn: (r) => r["_measurement"] == "%s")
                |> filter(fn (r) => r["_field"] == "%s")
                """.formatted(bucket, timestampInMinutes, device, field);
        return this.query(fluxQuery);
    }

    public List<Object> getById(String bucket, double timestampInMinutes, String device, String field, Long id) {
        String fluxQuery = """
                from(bucket: "%s")
                |> range(start: -%fm, stop: now())
                |> filter(fn: (r) => r["_measurement"] == "%s")
                |> filter(fn (r) => r["_field"] == "%s")
                |> filter(fn (r) => r["id"] == "%d")
                """.formatted(bucket, timestampInMinutes, device, field, id);
        return this.query(fluxQuery);
    }

    // TODO: Add property id as a tag when generating measurements for InfluxDB
    public List<Object> getByPropertyId(String bucket, double timestampInMinutes, String device, String field, Long propertyId) {
        String fluxQuery = """
                from(bucket: "%s")
                |> range(start: -%fm, stop: now())
                |> filter(fn: (r) => r["_measurement"] == "%s")
                |> filter(fn (r) => r["_field"] == "%s")
                |> filter(fn (r) => r["propertyId"] == "%d")
                """.formatted(bucket, timestampInMinutes, device, field, propertyId);
        return this.query(fluxQuery);
    }

}
