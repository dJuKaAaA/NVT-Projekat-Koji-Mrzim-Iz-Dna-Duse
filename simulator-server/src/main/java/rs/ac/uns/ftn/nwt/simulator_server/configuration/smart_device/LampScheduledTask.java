package rs.ac.uns.ftn.nwt.simulator_server.configuration.smart_device;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Slf4j
@RequiredArgsConstructor
@Configuration
public class LampScheduledTask {

//    private final Random random;
//    private final LampService lampService;
//    private final SimpMessagingTemplate template;
//    private final ObjectMapper mapper;
//    private final Collection<LampResponse> devicesSubscribed = new ArrayList<>();
//    private final IMqttClient mqttClient;
//    private final MessageCallback messageCallback;
//
//    @PostConstruct
//    public void postConstruction() {
//        this.messageCallback.setActiveDeviceCollector(this::setDevicesSubscribed);
//        this.messageCallback.setInactiveDeviceRemover(this::removeDevice);
//        devicesSubscribed.add(LampResponse.builder().id(1L).active(true).build());
//    }
//
//    @Scheduled(fixedRate = 2000, initialDelay = 5000)
//    public void generateMeasurement() throws JsonProcessingException, MqttException {
//        for (LampResponse device : devicesSubscribed) {
//            device.setLightLevel(random.nextFloat());
//            this.lampService.save(device, Instant.now());
//            mqttClient.publish(RECEIVE_LAMP_TOPIC + "-" + device.getId(), new MqttMessage(mapper.writeValueAsBytes(device)));
//            log.debug("Measurement generated");
//        }
//    }
//
//    // clear -> true, ako treba da se ponovo inicijalizuje lista
//    // clear -> false, ako treba samo da se doda novi element u listu bez brisanja vec postojecih
//    public void setDevicesSubscribed(Collection<Long> ids, boolean clear) {
//        if (clear) {
//            devicesSubscribed.clear();
//        }
//        for (Long id : ids) {
//            devicesSubscribed.add(LampResponse.builder().id(id).active(true).build());
//        }
//    }
//
//    public void removeDevice(long id) {
//        for (LampResponse lamp : devicesSubscribed) {
//            if (lamp.getId() == id) {
//                devicesSubscribed.remove(lamp);
//                break;
//            }
//        }
//    }
}
