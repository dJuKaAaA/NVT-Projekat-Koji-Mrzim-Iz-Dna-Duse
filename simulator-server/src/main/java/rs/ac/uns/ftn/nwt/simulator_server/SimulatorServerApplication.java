package rs.ac.uns.ftn.nwt.simulator_server;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.ReceiveTopicsConstants.*;

@RequiredArgsConstructor
@SpringBootApplication
public class SimulatorServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimulatorServerApplication.class, args);
	}

	private final IMqttClient mqttClient;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			mqttClient.subscribe(RECEIVE_AMBIENT_SENSOR_TOPIC, 2);
			mqttClient.subscribe(RECEIVE_SOLAR_PANEL_SYSTEM_TOPIC, 2);
			mqttClient.subscribe(RECEIVE_LAMP_TOPIC, 2);
			mqttClient.subscribe(RECEIVE_AIR_CONDITIONER_TOPIC, 2);
			mqttClient.subscribe(RECEIVE_VEHICLE_GATE_TOPIC, 2);
			mqttClient.subscribe(RECEIVE_SPRINKLER_SYSTEM_TOPIC, 2);
			mqttClient.subscribe(RECEIVE_ELECTRIC_VEHICLE_CHARGER_TOPIC, 2);
			mqttClient.subscribe(RECEIVE_WASHING_MACHINE_TOPIC, 2);
			mqttClient.subscribe(START_HEARTBEAT, 2);
		};
	}

}
