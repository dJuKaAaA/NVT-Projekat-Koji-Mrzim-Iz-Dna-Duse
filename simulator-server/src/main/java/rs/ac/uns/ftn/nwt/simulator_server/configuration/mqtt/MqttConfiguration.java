package rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Configuration
public class MqttConfiguration {

    @Value("${mqtt.connection}")
    private final String broker;
    @Value("${mqtt.username}")
    private final String username;
    @Value("${mqtt.password}")
    private final String password;
    private final String uniqueClientIdentifier = UUID.randomUUID().toString();
    private final MessageCallback messageCallback;

    @Bean
    @SneakyThrows
    public IMqttClient generateMqttClient() {
        MqttClient client = new MqttClient(this.broker, this.uniqueClientIdentifier, new MemoryPersistence());
        client.setCallback(messageCallback);
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setCleanStart(false);
        options.setAutomaticReconnect(true);
        options.setUserName(this.username);
        options.setPassword(Objects.requireNonNull(password).getBytes());
        client.connect(options);
        return client;
    }

}
