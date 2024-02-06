package rs.ac.uns.ftn.nwt.simulator_server.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Random;


@RequiredArgsConstructor
@Configuration
public class InfluxDbConfiguration {

    @Value("${url}")
    private final String url;

    @Value("${influxdb.token}")
    private final String token;

    @Value("${influxdb.organization}")
    private final String organization;

    @Value("${influxdb.bucket}")
    private final String bucket;

    @Bean
    public InfluxDBClient influxDbClient() {
        return InfluxDBClientFactory.create(this.url, this.token.toCharArray(),
                this.organization, this.bucket);
    }
}
