package nvt.project.smart_home.main.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url}")
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
