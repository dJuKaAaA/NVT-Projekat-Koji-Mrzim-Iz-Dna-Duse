package rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.nwt.simulator_server.constants.AmbientSensorCommand;
import rs.ac.uns.ftn.nwt.simulator_server.constants.air_conditioner.AirConditionerCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.electric_vehicle_charger.ElectricVehicleChargerCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.electric_vehicle_charger.ElectricVehicleChargerRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.solar_panel.SolarPanelSystemCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.solar_panel.SolarPanelSystemRequest;
import rs.ac.uns.ftn.nwt.simulator_server.constants.washing_machine.WashingMachineCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.HeartbeatDto;
import rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.request.LampRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.air_condtioner.AirConditionerRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.ambient_sensor.AmbientSensorRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.washing_machine.WashingMachineRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.request.SprinklerSystemRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.request.VehicleGateRequest;
import rs.ac.uns.ftn.nwt.simulator_server.service.*;
import rs.ac.uns.ftn.nwt.simulator_server.service.solar_panel_system.SolarPanelSystemService;

import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.ReceiveTopicsConstants.*;

@RequiredArgsConstructor
@Setter
@Component
public class MessageCallback implements MqttCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCallback.class);
    private final ObjectMapper objectMapper;

    // ambient sensor
    private AmbientSensorService.Callback startAmbientSensorSim;
    private AmbientSensorService.Callback cancelAmbientSensorSim;

    // solar panel system
    private SolarPanelSystemService.Callback startSolarPanelSystemSim;
    private SolarPanelSystemService.Callback cancelSolarPanelSystemSim;

    // air conditioner
    private AirConditionerService.Callback starAirConditionerNormalHeatingSim;
    private AirConditionerService.Callback starAirConditionerNormalCoolingSim;
    private AirConditionerService.Callback startAirConditionerNormalMaintenanceSim;

    private AirConditionerService.Callback startAirConditionerPeriodicHeatingSim;
    private AirConditionerService.Callback startAirConditionerPeriodicCollingSim;
    private AirConditionerService.Callback startAirConditionerPeriodicMaintenanceSim;

    private AirConditionerService.Callback offAirConditioner;

    // washing machine
    private WashingMachineService.Callback startWashingMachineOneTimeSim;
    private WashingMachineService.Callback scheduleWashingMachineSim;
    private WashingMachineService.Callback cancelWashingMachineSim;

    // lamp
    private LampService.Callback setLampCommand;
    // vehicle gate
    private VehicleGateService.Callback setVehicleGateCommand;
    // sprinkler system
    private SprinklerSystemService.Callback setSprinklerSystemCommand;

    // electric vehicle charger
    private ElectricVehicleChargerService.Callback startElectricVehicleChargerSim;
    private ElectricVehicleChargerService.Callback cancelElectricVehicleChargerSim;

    // hearbeat
    private HeartbeatService.Callback startHeartbeatSim;

    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        LOGGER.warn("disconnected: {}", mqttDisconnectResponse.getReasonString());
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {
        LOGGER.error("error: {}", e.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println(topic);
//
        if (topic.contains(RECEIVE_AMBIENT_SENSOR_TOPIC)) {
            AmbientSensorRequest request = objectMapper.readValue(mqttMessage.getPayload(), AmbientSensorRequest.class);
            switch (request.getCommand()) {
                case AmbientSensorCommand.ON -> startAmbientSensorSim.apply(request);
                case AmbientSensorCommand.OFF -> cancelAmbientSensorSim.apply(request);
            }
        }
        else if (topic.contains(RECEIVE_SOLAR_PANEL_SYSTEM_TOPIC)) {
            SolarPanelSystemRequest request = objectMapper.readValue(mqttMessage.getPayload(), SolarPanelSystemRequest.class);
            switch (request.getCommand()) {
                case SolarPanelSystemCommand.ON -> startSolarPanelSystemSim.apply(request);
                case SolarPanelSystemCommand.OFF -> cancelSolarPanelSystemSim.apply(request);
            }
        }
        else if(topic.contains(RECEIVE_AIR_CONDITIONER_TOPIC)) {
            AirConditionerRequest request = objectMapper.readValue(mqttMessage.getPayload(), AirConditionerRequest.class);
            switch (request.getCommand()) {
                case AirConditionerCommand.HEATING -> starAirConditionerNormalHeatingSim.apply(request);
                case AirConditionerCommand.COOLING -> starAirConditionerNormalCoolingSim.apply(request);
                case AirConditionerCommand.TEMPERATURE_MAINTENANCE -> startAirConditionerNormalMaintenanceSim.apply(request);

                case AirConditionerCommand.PERIODIC_HEATING -> startAirConditionerPeriodicHeatingSim.apply(request);
                case AirConditionerCommand.PERIODIC_COOLING -> startAirConditionerPeriodicCollingSim.apply(request);
                case AirConditionerCommand.PERIODIC_TEMPERATURE_MAINTENANCE -> startAirConditionerPeriodicMaintenanceSim.apply(request);
            }

            offAirConditioner.apply(request);
        }
        else if (topic.contains(RECEIVE_LAMP_TOPIC)) {
            LampRequest request = objectMapper.readValue(mqttMessage.getPayload(), LampRequest.class);
            setLampCommand.apply(request);
        }
        else if (topic.contains(RECEIVE_VEHICLE_GATE_TOPIC)) {
            VehicleGateRequest request = objectMapper.readValue(mqttMessage.getPayload(), VehicleGateRequest.class);
            setVehicleGateCommand.apply(request);
        }
        else if (topic.contains(RECEIVE_SPRINKLER_SYSTEM_TOPIC)) {
            SprinklerSystemRequest request = objectMapper.readValue(mqttMessage.getPayload(), SprinklerSystemRequest.class);
            setSprinklerSystemCommand.apply(request);
        } else if (topic.contains(RECEIVE_ELECTRIC_VEHICLE_CHARGER_TOPIC)) {
            ElectricVehicleChargerRequest request = objectMapper.readValue(mqttMessage.getPayload(), ElectricVehicleChargerRequest.class);
            switch (request.getCommand()) {
                case ElectricVehicleChargerCommand.ON -> startElectricVehicleChargerSim.apply(request);
                case ElectricVehicleChargerCommand.OFF -> cancelElectricVehicleChargerSim.apply(request);
            }
        }
        else if (topic.contains(RECEIVE_WASHING_MACHINE_TOPIC)) {
            WashingMachineRequest request = objectMapper.readValue(mqttMessage.getPayload(), WashingMachineRequest.class);

            switch (request.getCommand()) {
                case WashingMachineCommand.STANDARD_WASH_PROGRAM -> startWashingMachineOneTimeSim.apply(request);
                case WashingMachineCommand.COLOR_WASH_PROGRAM -> startWashingMachineOneTimeSim.apply(request);
                case WashingMachineCommand.WASH_PROGRAM_FOR_DELICATES -> startWashingMachineOneTimeSim.apply(request);

                case WashingMachineCommand.SCHEDULED_STANDARD_WASH_PROGRAM -> scheduleWashingMachineSim.apply(request);
                case WashingMachineCommand.SCHEDULED_COLOR_WASH_PROGRAM -> scheduleWashingMachineSim.apply(request);
                case WashingMachineCommand.SCHEDULED_WASH_PROGRAM_FOR_DELICATES -> scheduleWashingMachineSim.apply(request);

                case WashingMachineCommand.CANCEL -> cancelWashingMachineSim.apply(request);
            }
        } else if (topic.contains(START_HEARTBEAT)) {
            HeartbeatDto request = objectMapper.readValue(mqttMessage.getPayload(), HeartbeatDto.class);
            startHeartbeatSim.apply(request.getDeviceId());
        }

    }

    @Override
    public void deliveryComplete(IMqttToken iMqttToken) {
        LOGGER.debug("delivery complete, message id: {}", iMqttToken.getMessageId());
    }

    @Override
    public void connectComplete(boolean b, String s) {
        LOGGER.debug("connect complete, status: {} {}", b, s);
    }

    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {
        LOGGER.debug("auth packet arrived , status: {} {}", i, mqttProperties.getAuthenticationMethod());
    }
}
