package nvt.project.smart_home.test_script;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.RunnableManager;
import nvt.project.smart_home.main.core.constant.Role;
import nvt.project.smart_home.main.core.constant.devices.DeviceGroupType;
import nvt.project.smart_home.main.core.constant.devices.DeviceType;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.entity.City;
import nvt.project.smart_home.main.core.entity.Country;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.influxdb.InfluxDBQueryService;
import nvt.project.smart_home.main.core.repository.CityRepository;
import nvt.project.smart_home.main.core.repository.CountryRepository;
import nvt.project.smart_home.main.core.repository.UserRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.core.service.interf.IUserService;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCommand;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCurrentWorkMode;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.repository.AirConditionerHistoryRepository;
import nvt.project.smart_home.main.feature.device.air_conditioner.repository.AirConditionerRepository;
import nvt.project.smart_home.main.feature.device.ambient_sensor.entity.AmbientSensorEntity;
import nvt.project.smart_home.main.feature.device.ambient_sensor.repository.AmbientSensorRepository;
import nvt.project.smart_home.main.feature.device.ambient_sensor.service.impl.AmbientSensorService;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ChargingVehicleEntity;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ElectricVehicleChargerEntity;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.repository.ChargingVehicleRepository;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.repository.ElectricVehicleChargerRepository;
import nvt.project.smart_home.main.feature.device.home_battery.entity.HomeBatteryEntity;
import nvt.project.smart_home.main.feature.device.home_battery.repository.HomeBatteryRepository;
import nvt.project.smart_home.main.feature.device.home_battery.service.HomeBatterySimulatorService;
import nvt.project.smart_home.main.feature.device.solar_panel_system.entity.SolarPanelEntity;
import nvt.project.smart_home.main.feature.device.solar_panel_system.entity.SolarPanelSystemEntity;
import nvt.project.smart_home.main.feature.device.solar_panel_system.repository.SolarPanelSystemRepository;
import nvt.project.smart_home.main.feature.device.lamp.entity.LampEntity;
import nvt.project.smart_home.main.feature.device.lamp.repository.LampRepository;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemScheduleEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.repository.SprinklerSystemRepository;
import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;
import nvt.project.smart_home.main.feature.device.vehicle_gate.repository.VehicleGateRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCommand;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCurrentWorkMode;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingTime;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.repository.WashingMachineHistoryRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.repository.WashingMachineRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.service.impl.WashingMachineHistoryService;
import nvt.project.smart_home.main.feature.device.washing_machine.service.impl.WashingMachineService;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.constant.PropertyType;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import nvt.project.smart_home.test_script.ambient_sensor_utils.AmbientSensorDataEntry;
import nvt.project.smart_home.test_script.ambient_sensor_utils.AmbientSensorUtils;
import nvt.project.smart_home.test_script.solar_panel_system_utils.SolarPanelSystemUtils;
import org.jetbrains.annotations.NotNull;
import nvt.project.smart_home.test_script.lamp_utils.LampInfluxDBDataGenerator;
import nvt.project.smart_home.test_script.sprinkler_system_utils.SprinklerSystemInfluxDBDataGenerator;
import nvt.project.smart_home.test_script.vehicle_gate_utils.VehicleGateInfluxDBDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static nvt.project.smart_home.main.core.constant.devices.DeviceType.*;
import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.HOME_BATTERY_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.TagsConstants.*;
import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.AIR_CONDITIONER_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.DeviceNamesConstants.AMBIENT_SENSOR_DEVICE_NAME;
import static nvt.project.smart_home.main.core.constant.influxdb.InfluxDbFieldsConstants.*;
import static nvt.project.smart_home.test_script.ambient_sensor_utils.AmbientSensorUtils.isWithinDateInterval;

@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = {"nvt.project.smart_home"})
public class InsertDataScript {

    @Value("${file.separator}")
    private final String separator;

    @Value("${directory.path.init.images}")
    private final String directoryInitImages;

    @Value("${superadmin.init.password.file}")
    private final String superAdminPasswordFile;

    @Value("${super.admin.profile.image.name}")
    private final String superAdminProfileImage;

    private final Random random = new Random();

    private final int NUMBER_OF_AMBIENT_SENSORS = 1;
    private final int NUMBER_OF_WASHING_MACHINES = 1;
    private final int NUMBER_OF_AIR_CONDITIONERS = 1;

    private AmbientSensorEntity TEST_AMBIENT_SENSOR_1;
    private AmbientSensorEntity TEST_AMBIENT_SENSOR_2;

    private WashingMachineEntity TEST_WASHING_MACHINE_1;
    private WashingMachineEntity TEST_WASHING_MACHINE_2;

    private SolarPanelSystemEntity TEST_SOLAR_PANEL_SYSTEM_1;
    private SolarPanelSystemEntity TEST_SOLAR_PANEL_SYSTEM_2;

    private HomeBatteryEntity TEST_HOME_BATTERY_1;
    private HomeBatteryEntity TEST_HOME_BATTERY_2;

    private ElectricVehicleChargerEntity TEST_ELECTRIC_VEHICLE_CHARGER_1;
    private ElectricVehicleChargerEntity TEST_ELECTRIC_VEHICLE_CHARGER_2;
    private AirConditionerEntity TEST_AIR_CONDITIONER_1;
    private AirConditionerEntity TEST_AIR_CONDITIONER_2;

    private LampEntity LAMP_1;
    private LampEntity LAMP_2;

    private VehicleGateEntity VEHICLE_GATE_1;
    private VehicleGateEntity VEHICLE_GATE_2;

    private SprinklerSystemEntity SPRINKLER_SYSTEM_1;
    private SprinklerSystemEntity SPRINKLER_SYSTEM_2;

    private UserEntity TEST_BOBO_USER;
    private UserEntity TEST_GORAN_USER;

    private Property TEST_PROPERTY;
    private City TEST_CITY;


    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    private final IUserService userService;
    private final IImageService imageService;


    private final AmbientSensorRepository ambientSensorRepository;
    private final WashingMachineRepository washingMachineRepository;
    private final WashingMachineHistoryRepository washingMachineHistoryRepository;
    private final SolarPanelSystemRepository solarPanelSystemRepository;
    private final HomeBatteryRepository homeBatteryRepository;
    private final ElectricVehicleChargerRepository electricVehicleChargerRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final ChargingVehicleRepository chargingVehicleRepository;


    private final AmbientSensorService ambientSensorService;
    private final WashingMachineService washingMachineService;
    private final WashingMachineHistoryService washingMachineHistoryService;

    private final HomeBatterySimulatorService homeBatterySimulatorService;
    private final RunnableManager runnableManager;
    private final AirConditionerRepository airConditionerRepository;
    private final AirConditionerHistoryRepository airConditionerHistoryRepository;

    private final InfluxDBQueryService influxDBQueryService;
    private final PasswordEncoder passwordEncoder;

    private final SprinklerSystemRepository sprinklerSystemRepository;
    private final VehicleGateRepository vehicleGateRepository;
    private final LampRepository lampRepository;


    @Value("${ambient.sensor.test.data.file}")
    private final String csvAmbientSensorFile;

    public static void main(String[] args) {
        var context = SpringApplication.run(InsertDataScript.class, args);
//        context.close();
    }

    @SneakyThrows
    @PostConstruct
    public void generateTestData() {
        System.out.println("### Init Test Data ###");
        System.out.println("### Start time: " + LocalDateTime.now());

//        insertSuperAdmin();
//        insertTestUsers();
//        insertTestProperties();
//        insertTestAmbientSensors();
//        insertTestWashingsMachines();
//        insertAirConditioners();
//        insertTestSprinklerSystems();
//        insertTestVehicleGates();
//        insertTestLamps();
//        insertSolarPanelSystemEntities();
//        insertHomeBatteryEntities();
//        insertElectricVehicleChargerEntities();
//        insertPowerConsumptionTestData();

        System.out.println("### End time: " + LocalDateTime.now());
    }

    // ### SUPER ADMIN ###
    private void insertSuperAdmin() throws IOException {
        String superAdminEmail = "ivanmartic311@gmail.com";
        Optional<UserEntity> admin = userService.findByEmail(superAdminEmail);
        if (admin.isPresent()) return;


        String superAdminPassword = "Ivan1234"; // generatePassword()
        savePasswordToFile(superAdminPasswordFile, superAdminPassword);

        String superAdminProfileImagePath = directoryInitImages + separator + superAdminProfileImage;
        ImageRequestDto image = imageService.readImageFromFileSystemInRequestDtoFormat(superAdminProfileImagePath);

        ImageRequestDto profileImage = ImageRequestDto.builder()
                .name(superAdminEmail)
                .format("jpg")
                .base64FormatString(image.getBase64FormatString())
                .build();

        UserEntity superAdmin = UserEntity.builder()
                .name("Ivan")
                .email(superAdminEmail)
                .role(Role.SUPER_ADMIN)
                .enabled(true)
                .password(passwordEncoder.encode(superAdminPassword))
                .build();
        userService.save(superAdmin, profileImage);
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void savePasswordToFile(String filePath, String password) throws IOException {

        File file = new File(filePath);

        if (!file.exists()) {
            file.createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(password);
            System.out.println("Password successfully written to file: " + filePath);
        }
    }

    // ### USERS ###
    private void insertTestUsers() {
        generateBoboNormalUser();
        generateGoranUser();
    }

    private void generateBoboNormalUser() {
        TEST_BOBO_USER = userRepository.save(UserEntity.builder()
                .name("Bobo Bobic")
                .email("bobo@email.com")
                .password(passwordEncoder.encode("bobo"))
                .role(Role.USER)
                .enabled(true)
                .build());
    }

    private void generateGoranUser() {
        TEST_GORAN_USER = userRepository.save(UserEntity.builder()
                .name("Goran Goric")
                .email("goran@email.com")
                .password(passwordEncoder.encode("goran"))
                .role(Role.USER)
                .enabled(true)
                .build());
    }

    // ### PROPERTIES ###
    private void insertTestProperties() {
        generateBoboProperty();
    }

    private void generateBoboProperty() {
        Country country = countryRepository.save(Country.builder().name("Srbija").build());
        TEST_CITY = cityRepository.save(City.builder()
                .name("Novi Sad")
                .country(country)
                .build());
        TEST_PROPERTY = propertyRepository.save(Property.builder()
                .name("Kuca")
                .owner(TEST_BOBO_USER)
                .floors(2)
                .area(100.0)
                .longitude(42.0)
                .latitude(19.0)
                .address("Adresa")
                .city(TEST_CITY)
                .type(PropertyType.HOUSE)
                .status(PropertyStatus.APPROVED)
                .build());
    }

    // ### AMBIENT SENSORS ###
    @SneakyThrows
    private void insertTestAmbientSensors() {
        generateAmbientSensorsWithData();
    }

    private void generateAmbientSensorsWithData() {
        IntStream.range(0, 2)
                .parallel()
                .forEach(i -> {
                    AmbientSensorEntity ambientSensor = ambientSensorRepository.save(AmbientSensorEntity.builder()
                            .name("Ambient sensor test" + i)
                            .property(TEST_PROPERTY)
                            .deviceType(DeviceType.AMBIENT_SENSOR)
                            .powerConsumption(139.2)
                            .groupType(DeviceGroupType.PKA)
                            .build());
                    ambientSensorService.setActivity(ambientSensor.getId(), true);

                    if (i == 0) {
                        TEST_AMBIENT_SENSOR_1 = ambientSensor;
                        insertAmbientSensorTestData(TEST_AMBIENT_SENSOR_1);
                    } else if (i == 1) {
                        TEST_AMBIENT_SENSOR_2 = ambientSensor;
                        insertAmbientSensorTestData(TEST_AMBIENT_SENSOR_2);
                    }
                });
    }

    private void generateAmbientSensorsWithoutTestData() {
        IntStream.range(0, NUMBER_OF_AMBIENT_SENSORS)
                .parallel()
                .forEach(i -> {
                    AmbientSensorEntity ambientSensor = ambientSensorRepository.save(AmbientSensorEntity.builder()
                            .name("Ambient sensor test" + i + 2)
                            .property(TEST_PROPERTY)
                            .deviceType(DeviceType.AMBIENT_SENSOR)
                            .powerConsumption(151.5)
                            .groupType(DeviceGroupType.PKA)
                            .build());
                    ambientSensorService.setActivity(ambientSensor.getId(), true);
                });
    }

    @SneakyThrows
    private void insertAmbientSensorTestData(AmbientSensorEntity ambientSensor) {
        System.out.println("INSERT AMBIENT SENSORS TEST DATA FOR DEVICE-" + ambientSensor.getId());
        LocalDateTime now = LocalDateTime.now();
        String month = AmbientSensorUtils.formatWithLeadingZero(now.getMonthValue());
        String day = AmbientSensorUtils.formatWithLeadingZero(now.getDayOfMonth());
        String hours = AmbientSensorUtils.formatWithLeadingZero(now.getHour());
        String minutes = AmbientSensorUtils.formatWithLeadingZero(now.getMinute());

        LocalDateTime startDate = LocalDateTime.parse("2015-%s-%sT%s:%s".formatted(month, day, hours, minutes));
        LocalDateTime endDate = startDate.plusDays(90);


        final AtomicReference<Instant> instantRef = new AtomicReference<>(Instant.now());
        AtomicBoolean shouldBreak = new AtomicBoolean(false); // Za kontrolu prekida
        Stream<String> stream = Files.lines(Paths.get(csvAmbientSensorFile));

        stream
                .skip(1)
                .parallel()
                .takeWhile(s -> !shouldBreak.get())
                .forEach(line -> processLine(line, startDate, endDate, instantRef, ambientSensor.getId(), shouldBreak)
                );
    }

    private void processLine(String line, LocalDateTime startDate, LocalDateTime endDate,
                             AtomicReference<Instant> instantRef, long ambientSensorId, AtomicBoolean shouldBreak) {
        String[] parts = line.split(",");
        AmbientSensorDataEntry entry = AmbientSensorUtils.parseDataEntry(parts);

        if (isWithinDateInterval(entry.getDateTime(), startDate, endDate)) {
            Instant instant = instantRef.getAndUpdate(prevInstant -> prevInstant.minusSeconds(10 * 60));
            influxDBQueryService.save(ambientSensorId, AMBIENT_SENSOR_DEVICE_NAME, AMBIENT_SENSOR_FIELD_TEMPERATURE, entry.getTemperature(), instant);
            influxDBQueryService.save(ambientSensorId, AMBIENT_SENSOR_DEVICE_NAME, AMBIENT_SENSOR_FIELD_HUMIDITY, entry.getHumidity(), instant);

        } else if (entry.getDateTime().isAfter(endDate)) shouldBreak.set(true);
    }

    // ### WASHING MACHINES ###
    private void insertTestWashingsMachines() {
        generateWashingMachinesWithTestData();
    }

    private void generateWashingMachinesWithoutTestData() {
        IntStream.range(0, NUMBER_OF_WASHING_MACHINES)
                .parallel()
                .forEach(i -> {
                    WashingMachineEntity washingMachine = washingMachineRepository.save(WashingMachineEntity.builder()
                            .name("Ves masina" + i + 2)
                            .property(TEST_PROPERTY)
                            .deviceType(WASHING_MACHINE)
                            .powerConsumption(202.5)
                            .groupType(DeviceGroupType.PKA)
                            .workMode(WashingMachineCurrentWorkMode.OFF)
                            .build());
                });
    }

    private void generateWashingMachinesWithTestData() {
        IntStream.range(0, 2)
                .parallel()
                .forEach(i -> {
                    WashingMachineEntity washingMachine = washingMachineRepository.save(WashingMachineEntity.builder()
                            .name("Ves masina" + i)
                            .property(TEST_PROPERTY)
                            .deviceType(WASHING_MACHINE)
                            .powerConsumption(198.5)
                            .groupType(DeviceGroupType.PKA)
                            .workMode(WashingMachineCurrentWorkMode.OFF)
                            .build());

                    if (i == 0) {
                        TEST_WASHING_MACHINE_1 = washingMachine;
                        insertHistoryTestData(TEST_WASHING_MACHINE_1);
                    } else if (i == 1) {
                        TEST_WASHING_MACHINE_2 = washingMachine;
                        insertHistoryTestData(TEST_WASHING_MACHINE_2);
                    }
                });
    }

    private void insertHistoryTestData(WashingMachineEntity washingMachineEntity) {
        System.out.println("INSERT WASHING MACHINES TEST DATA FOR DEVICE-" + washingMachineEntity.getId());
        int randomNumber = this.random.nextInt(0, 500);
        var now = LocalDateTime.now();

        List<WashingMachineAppointmentHistoryEntity> history = new ArrayList<>();
        IntStream.range(0, 90).forEach(i -> {

            WashingMachineAppointmentHistoryEntity history1 = WashingMachineAppointmentHistoryEntity.builder()
                    .executor(TEST_BOBO_USER.getEmail())
                    .timestamp(now
                            .minusDays(90 - i)
                            .minusMinutes(30)
                            .minusSeconds(WashingTime.STANDARD_WASH_PROGRAM_IN_SECONDS)
                            .minusMinutes(randomNumber))
                    .device(washingMachineEntity)
                    .action("ON_" + String.valueOf(WashingMachineCommand.SCHEDULED_STANDARD_WASH_PROGRAM))
                    .build();
            history.add(history1);

            WashingMachineAppointmentHistoryEntity history2 = WashingMachineAppointmentHistoryEntity.builder()
                    .executor("WASHING_MACHINE")
                    .timestamp(now
                            .minusDays(90 - i)
                            .minusMinutes(30)
                            .minusMinutes(randomNumber))
                    .device(washingMachineEntity)
                    .action("OFF")
                    .build();
            history.add(history2);

            WashingMachineAppointmentHistoryEntity history3 = WashingMachineAppointmentHistoryEntity.builder()
                    .executor(TEST_BOBO_USER.getEmail())
                    .timestamp(now
                            .minusDays(90 - i)
                            .minusSeconds(WashingTime.COLOR_WASH_PROGRAM_IN_SECONDS)
                            .minusMinutes(randomNumber))
                    .device(washingMachineEntity)
                    .action("ON_" + String.valueOf(WashingMachineCommand.COLOR_WASH_PROGRAM))
                    .build();
            history.add(history3);

            WashingMachineAppointmentHistoryEntity history4 = WashingMachineAppointmentHistoryEntity.builder()
                    .executor("WASHING_MACHINE")
                    .timestamp(now
                            .minusDays(90 - i)
                            .minusMinutes(randomNumber))
                    .device(washingMachineEntity)
                    .action("OFF")
                    .build();
            history.add(history4);

        });
        washingMachineHistoryRepository.saveAll(history);
    }

    // ### AIR CONDITIONERS ###
    private void insertTestAirConditioners() {
        insertAirConditioners();
        insertHistoryDataForAirConditioner(TEST_AIR_CONDITIONER_1);
        insertHistoryDataForAirConditioner(TEST_AIR_CONDITIONER_2);
    }

    private void insertAirConditioners() {
        TEST_AIR_CONDITIONER_1 = airConditionerRepository.save(AirConditionerEntity.builder()
                .name("Air Conditioner 1")
                .minTemperature(16)
                .maxTemperature(29)
                .property(TEST_PROPERTY)
                .deviceType(AIR_CONDITIONER)
                .powerConsumption(0.5)
                .groupType(DeviceGroupType.PKA)
                .workMode(AirConditionerCurrentWorkMode.OFF)
                .currentWorkTemperature(null)
                .build());

        TEST_AIR_CONDITIONER_2 = airConditionerRepository.save(AirConditionerEntity.builder()
                .name("Air Conditioner 2")
                .property(TEST_PROPERTY)
                .deviceType(AIR_CONDITIONER)
                .minTemperature(16)
                .maxTemperature(29)
                .powerConsumption(0.5)
                .groupType(DeviceGroupType.PKA)
                .workMode(AirConditionerCurrentWorkMode.OFF)
                .currentWorkTemperature(null)
                .build());
    }

    private void insertHistoryDataForAirConditioner(AirConditionerEntity entity) {
        System.out.println("INSERT AIR CONDITIONER TEST DATA FOR DEVICE-");
        int randomNumber = this.random.nextInt(0, 500);
        var now = LocalDateTime.now();

        IntStream.range(0, 90).forEach(i -> {

            int temperature = this.random.nextInt(21, 24);
            AirConditionerAppointmentHistoryEntity history1 = airConditionerHistoryRepository.save(AirConditionerAppointmentHistoryEntity.builder()
                    .executor(TEST_BOBO_USER.getEmail())
                    .timestamp(now
                            .minusDays(90 - i)
                            .minusMinutes(30)
                            .minusMinutes(randomNumber))
                    .device(entity)
                    .action("ON_" + AirConditionerCommand.HEATING)
                    .build());
            for (int j = 1; j < 4; j++) {
                LocalDateTime startWorkTime = now
                        .minusDays(90 - i)
                        .minusMinutes(30)
                        .minusMinutes(randomNumber);
                var timestamp = startWorkTime.plusMinutes(j * 10);
                Instant instant = timestamp.atZone(ZoneOffset.UTC).toInstant();
                influxDBQueryService.save(entity.getId(),
                        AIR_CONDITIONER_DEVICE_NAME,
                        AIR_CONDITIONER_FIELD_TEMPERATURE,
                        temperature + j, instant);
            }

            AirConditionerAppointmentHistoryEntity history2 = airConditionerHistoryRepository.save(AirConditionerAppointmentHistoryEntity.builder()
                    .executor(TEST_BOBO_USER.getEmail())
                    .timestamp(now
                            .minusDays(90 - i)
                            .minusMinutes(30)
                            .minusMinutes(randomNumber))
                    .device(entity)
                    .action("OFF_" + AirConditionerCommand.HEATING)
                    .build());

            AirConditionerAppointmentHistoryEntity history3 = airConditionerHistoryRepository.save(AirConditionerAppointmentHistoryEntity.builder()
                    .executor(TEST_BOBO_USER.getEmail())
                    .timestamp(now
                            .minusDays(90 - i)
                            .minusMinutes(30)
                            .minusMinutes(randomNumber))
                    .device(entity)
                    .action("ON_" + AirConditionerCommand.COOLING)
                    .build());

            AirConditionerAppointmentHistoryEntity history4 = airConditionerHistoryRepository.save(airConditionerHistoryRepository.save(AirConditionerAppointmentHistoryEntity.builder()
                    .executor(TEST_BOBO_USER.getEmail())
                    .timestamp(now
                            .minusDays(90 - i)
                            .minusMinutes(randomNumber))
                    .device(entity)
                    .action("OFF" + AirConditionerCommand.COOLING)
                    .build()));

            for (int j = 1; j < 4; j++) {
                LocalDateTime startWorkTime = now
                        .minusDays(90 - i)
                        .minusMinutes(30)
                        .minusMinutes(randomNumber);
                var timestamp = startWorkTime.plusMinutes(j * 10);
                Instant instant = timestamp.atZone(ZoneOffset.UTC).toInstant();
                influxDBQueryService.save(entity.getId(),
                        AIR_CONDITIONER_DEVICE_NAME,
                        AIR_CONDITIONER_FIELD_TEMPERATURE,
                        temperature - j, instant);
            }

        });
    }

    private void insertPowerConsumptionTestData() {

        final int DATA_GEN_TIME_IN_MINUTES = 30 * 24 * 90;
        final Instant dataGenTimeTimestamp = Instant.now().minusSeconds(DATA_GEN_TIME_IN_MINUTES * 60);

        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();
        Lock lock3 = new ReentrantLock();
        IntStream.range(0, DATA_GEN_TIME_IN_MINUTES)    // tri mjeseca svaki minut
                .parallel()
                .forEach(i -> {
                    // stavi da se pune 3 vozila svaki dan
                    Instant timestamp = dataGenTimeTimestamp.plusSeconds(i * 60L);

                    double consumption = 0.0;

                    // prvi ambijentalni senzor
                    double ambientSensor1Consumption = TEST_AMBIENT_SENSOR_1.getPowerConsumption() / 60;
                    ambientSensor1Consumption += ambientSensor1Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += ambientSensor1Consumption;
                    writeConsumedEnergyToInfluxDB(
                            TEST_AMBIENT_SENSOR_1.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            ambientSensor1Consumption,
                            TEST_AMBIENT_SENSOR_1.getDeviceType(),
                            timestamp
                    );

                    // drugi ambijentalni senzor
                    double ambientSensor2Consumption = TEST_AMBIENT_SENSOR_2.getPowerConsumption() / 60;
                    ambientSensor2Consumption += ambientSensor2Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += ambientSensor2Consumption;
                    writeConsumedEnergyToInfluxDB(
                            TEST_AMBIENT_SENSOR_2.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            ambientSensor2Consumption,
                            TEST_AMBIENT_SENSOR_2.getDeviceType(),
                            timestamp
                    );

                    // prva ves masina
                    double washingMachine1Consumption = TEST_WASHING_MACHINE_1.getPowerConsumption() / 60;
                    washingMachine1Consumption += washingMachine1Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += washingMachine1Consumption;
                    writeConsumedEnergyToInfluxDB(
                            TEST_WASHING_MACHINE_1.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            washingMachine1Consumption,
                            TEST_WASHING_MACHINE_1.getDeviceType(),
                            timestamp
                    );

                    // druga ves masina
                    double washingMachine2Consumption = TEST_WASHING_MACHINE_2.getPowerConsumption() / 60;
                    washingMachine2Consumption += washingMachine2Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += washingMachine2Consumption;
                    writeConsumedEnergyToInfluxDB(
                            TEST_WASHING_MACHINE_2.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            washingMachine2Consumption,
                            TEST_WASHING_MACHINE_2.getDeviceType(),
                            timestamp
                    );

                    // prva lampa
                    double lamp1Consumption = LAMP_1.getPowerConsumption() / 60.0;
                    lamp1Consumption += lamp1Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += lamp1Consumption;
                    writeConsumedEnergyToInfluxDB(
                            LAMP_1.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            lamp1Consumption,
                            LAMP_1.getDeviceType(),
                            timestamp
                    );

                    // druga lampa
                    double lamp2Consumption = LAMP_2.getPowerConsumption() / 60.0;
                    lamp2Consumption += lamp2Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += lamp2Consumption;
                    writeConsumedEnergyToInfluxDB(
                            LAMP_2.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            lamp2Consumption,
                            LAMP_2.getDeviceType(),
                            timestamp
                    );

                    // prva kapija
                    double vehicleGate1Consumption = VEHICLE_GATE_1.getPowerConsumption() / 60.0;
                    vehicleGate1Consumption += vehicleGate1Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += vehicleGate1Consumption;
                    writeConsumedEnergyToInfluxDB(
                            VEHICLE_GATE_1.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            vehicleGate1Consumption,
                            VEHICLE_GATE_1.getDeviceType(),
                            timestamp
                    );

                    // druga kapija
                    double vehicleGate2Consumption = VEHICLE_GATE_2.getPowerConsumption() / 60.0;
                    vehicleGate2Consumption += vehicleGate2Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += vehicleGate2Consumption;
                    writeConsumedEnergyToInfluxDB(
                            VEHICLE_GATE_2.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            vehicleGate2Consumption,
                            VEHICLE_GATE_2.getDeviceType(),
                            timestamp
                    );

                    // prva prskalica
                    double sprinklerSystem1Consumption = SPRINKLER_SYSTEM_1.getPowerConsumption() / 60.0;
                    sprinklerSystem1Consumption += sprinklerSystem1Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += sprinklerSystem1Consumption;
                    writeConsumedEnergyToInfluxDB(
                            SPRINKLER_SYSTEM_1.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            sprinklerSystem1Consumption,
                            SPRINKLER_SYSTEM_1.getDeviceType(),
                            timestamp
                    );

                    // druga prskalica
                    double sprinklerSystem2Consumption = SPRINKLER_SYSTEM_2.getPowerConsumption() / 60.0;
                    sprinklerSystem2Consumption += sprinklerSystem2Consumption * ((random.nextDouble() * 0.4) - 0.2);
                    consumption += sprinklerSystem2Consumption;
                    writeConsumedEnergyToInfluxDB(
                            SPRINKLER_SYSTEM_2.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            sprinklerSystem2Consumption,
                            SPRINKLER_SYSTEM_2.getDeviceType(),
                            timestamp
                    );

                    // sistem solarnih panela
                    Calendar target = Calendar.getInstance();
                    target.setTimeInMillis(timestamp.toEpochMilli());
                    Calendar noon = SolarPanelSystemUtils.getCalendar(target, 12, 0, 0);

                    // prvi sistem solarnih panela
                    double energy1 = generatePowerFromSolarPanelSystem(
                            TEST_SOLAR_PANEL_SYSTEM_1,
                            noon,
                            target
                    );
                    consumption -= energy1;
                    influxDBQueryService.save(
                            TEST_SOLAR_PANEL_SYSTEM_1.getId(),
                            SOLAR_PANEL_SYSTEM_DEVICE_NAME,
                            SOLAR_PANEL_SYSTEM_FIELD_ENERGY,
                            energy1,
                            timestamp
                    );

                    // drugi sistem solarnih panela
                    double energy2 = generatePowerFromSolarPanelSystem(
                            TEST_SOLAR_PANEL_SYSTEM_2,
                            noon,
                            target
                    );
                    consumption -= energy2;
                    influxDBQueryService.save(
                            TEST_SOLAR_PANEL_SYSTEM_2.getId(),
                            SOLAR_PANEL_SYSTEM_DEVICE_NAME,
                            SOLAR_PANEL_SYSTEM_FIELD_ENERGY,
                            energy2,
                            timestamp
                    );

                    // prvi punjac elecktricnih vozila
                    double electricCharger1Charge = getChargeValueForVehicle(TEST_ELECTRIC_VEHICLE_CHARGER_1.getVehiclesCharging().get(0), lock1);
                    consumption += electricCharger1Charge;
                    writeConsumedEnergyToInfluxDB(
                            TEST_ELECTRIC_VEHICLE_CHARGER_1.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            electricCharger1Charge,
                            TEST_ELECTRIC_VEHICLE_CHARGER_1.getDeviceType(),
                            timestamp
                    );

                    // drugi punjac elecktricnih vozila
                    double electricCharger2Charge = getChargeValueForVehicle(TEST_ELECTRIC_VEHICLE_CHARGER_2.getVehiclesCharging().get(0), lock2);
                    consumption += electricCharger2Charge;
                    writeConsumedEnergyToInfluxDB(
                            TEST_ELECTRIC_VEHICLE_CHARGER_2.getId(),
                            TEST_PROPERTY.getId(),
                            TEST_CITY.getId(),
                            electricCharger2Charge,
                            TEST_ELECTRIC_VEHICLE_CHARGER_2.getDeviceType(),
                            timestamp
                    );

                    // praznjenje/punjenje baterije
                    lock3.lock();
                    try {
                        double excessPower = consumePowerFromBatteries(
                                TEST_HOME_BATTERY_1,
                                TEST_PROPERTY.getId(),
                                consumption,
                                timestamp
                        );
                        excessPower = consumePowerFromBatteries(
                                TEST_HOME_BATTERY_2,
                                TEST_PROPERTY.getId(),
                                excessPower,
                                timestamp
                        );
                        if (excessPower != 0.0) {
                            Map<String, String> excessTags = new HashMap<>();
                            excessTags.put(TAG_KEY_PROPERTY_ID, String.valueOf(TEST_PROPERTY.getId()));
                            excessTags.put(HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE, TAG_VALUE_CONSUMED_TAKEN_FROM_NETWORK);
                            influxDBQueryService.save(HOME_BATTERY_DEVICE_NAME, HOME_BATTERY_FIELD_POWER_CONSUMPTION, excessPower, timestamp, excessTags);
                        }

                    } catch (Exception _) {

                    } finally {
                        lock3.unlock();
                    }
                });
    }

    private double getChargeValueForVehicle(ChargingVehicleEntity chargingVehicle, Lock lock) {
        double currVehiclePower = chargingVehicle.getCurrentPower();
        double chargeValue = getChargeValue(
                TEST_ELECTRIC_VEHICLE_CHARGER_1.getChargeLimit(),
                currVehiclePower,
                chargingVehicle.getMaxPower(),
                TEST_ELECTRIC_VEHICLE_CHARGER_1.getChargePower());
        lock.lock();
        try {
            chargingVehicle.setCurrentPower(chargingVehicle.getCurrentPower() + chargeValue);
            chargingVehicleRepository.save(chargingVehicle);
        } catch (Exception _) {

        } finally {
            lock.unlock();
        }
        return chargeValue;
    }

    private void writeConsumedEnergyToInfluxDB(long deviceId, long propertyId, long cityId, double consumedEnergy, DeviceType deviceType, Instant timestamp) {
        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));

        tags.put(TAG_KEY_CITY_ID, String.valueOf(cityId));
        if (consumedEnergy < 0) {
            tags.put(TAG_KEY_POWER_CONSUMPTION_TYPE, TAG_KEY_POWER_CONSUMPTION_GENERATED);
        } else {
            tags.put(TAG_KEY_POWER_CONSUMPTION_TYPE, TAG_KEY_POWER_CONSUMPTION_CONSUMED);
        }
        influxDBQueryService.save(deviceId, homeBatterySimulatorService.getDeviceName(deviceType), DEVICE_POWER_CONSUMPTION_FIELD, consumedEnergy, timestamp, tags);
    }

    private void insertSolarPanelSystemEntities() {
        final double PANEL_AREA = 10.0;
        final double PANEL_EFFICIENCY = 0.99;
        final int PANEL_COUNT = 50;
        IntStream.range(0, 2)
                .parallel()
                .forEach(i -> {
                    List<SolarPanelEntity> panels = new ArrayList<>();
                    for (int j = 0; j < PANEL_COUNT; j++) {
                        panels.add(SolarPanelEntity.builder().area(PANEL_AREA).efficiency(PANEL_EFFICIENCY).build());
                    }
                    SolarPanelSystemEntity solarPanelSystem = solarPanelSystemRepository.save(SolarPanelSystemEntity.builder()
                            .name("Sistem solarnih panela " + (i + 1))
                            .property(TEST_PROPERTY)
                            .deviceType(SOLAR_PANEL_SYSTEM)
                            .deviceActive(false)
                            .groupType(DeviceGroupType.VEU)
                            .powerConsumption(0.0)
                            .solarPanels(panels)
                            .latitude(45.25624106360835)
                            .longitude(19.845573538131212)
                            .build());
                    if (i == 0) {
                        TEST_SOLAR_PANEL_SYSTEM_1 = solarPanelSystem;
                    } else if (i == 1) {
                        TEST_SOLAR_PANEL_SYSTEM_2 = solarPanelSystem;
                    }
                });
    }

    private void insertHomeBatteryEntities() {
        IntStream.range(0, 2)
                .parallel()
                .forEach(i -> {
                    HomeBatteryEntity homeBattery = homeBatteryRepository.save(HomeBatteryEntity.builder()
                            .name("Kucna baterija " + (i + 1))
                            .property(TEST_PROPERTY)
                            .deviceType(HOME_BATTERY)
                            .deviceActive(true)
                            .groupType(DeviceGroupType.VEU)
                            .powerConsumption(0.0)
                            .current(10000.0)
                            .capacity(10000.0)
                            .build());
                    if (i == 0) {
                        TEST_HOME_BATTERY_1 = homeBattery;
                    } else if (i == 1) {
                        TEST_HOME_BATTERY_2 = homeBattery;
                    }
                });
    }

    private void insertElectricVehicleChargerEntities() {
        IntStream.range(0, 2)
                .parallel()
                .forEach(i -> {
                    ElectricVehicleChargerEntity electricVehicleCharger = electricVehicleChargerRepository.save(ElectricVehicleChargerEntity.builder()
                            .name("Punjac za elektricna vozila " + (i + 1))
                            .property(TEST_PROPERTY)
                            .deviceType(ELECTRIC_VEHICLE_CHARGER)
                            .deviceActive(true)
                            .groupType(DeviceGroupType.VEU)
                            .powerConsumption(0.0)
                            .chargePower(5.0)
                            .chargerCount(3)
                            .chargersOccupied(3)
                            .chargeLimit(90.0)
                            .vehiclesCharging(List.of(
                                    ChargingVehicleEntity.builder().currentPower(0.0).maxPower(1000000.0).build()
                            ))
                            .build());
                    if (i == 0) {
                        TEST_ELECTRIC_VEHICLE_CHARGER_1 = electricVehicleCharger;
                    } else if (i == 1) {
                        TEST_ELECTRIC_VEHICLE_CHARGER_2 = electricVehicleCharger;
                    }
                });
    }

    private void turnOnHomeBatteries() {
        homeBatterySimulatorService.addBattery(TEST_HOME_BATTERY_1.getId(), TEST_HOME_BATTERY_1);
        homeBatterySimulatorService.addBattery(TEST_HOME_BATTERY_2.getId(), TEST_HOME_BATTERY_2);
    }

    private void startHomeBatterySimulations() {
        Runnable batterySimRunnable = homeBatterySimulatorService::simulateOneMinutePowerConsumption;
        runnableManager.startNonEndingRunnable(batterySimRunnable, 0, 60);
    }

    @SneakyThrows
    private double generatePowerFromSolarPanelSystem(SolarPanelSystemEntity solarPanelSystem, Calendar noon, Calendar target) {
        Location location = new Location(solarPanelSystem.getLatitude(), solarPanelSystem.getLongitude()); // Example: San Francisco

        // Create a SunriseSunsetCalculator for the specified location
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, target.getTimeZone());

        // Get today's sunrise and sunset times
        Calendar sunrise = calculator.getOfficialSunriseCalendarForDate(target);
        Calendar sunset = calculator.getOfficialSunsetCalendarForDate(target);

        // Calculate the duration of sunlight in minutes
        long sunHoursInMinutes = calculateSunlightDuration(sunrise, sunset);

        double maxEnergyProduced = 0.0;
        for (SolarPanelEntity panel : solarPanelSystem.getSolarPanels()) {
            double energyProduced = (double) sunHoursInMinutes / 60.0 * panel.getEfficiency() * panel.getArea();
//            energyProduced /= 1000.0;   // getting the kW
            maxEnergyProduced += energyProduced;
        }

        return SolarPanelSystemUtils.calculateProducedEnergy(
                sunrise,
                noon,
                target,
                sunset,
                maxEnergyProduced
        );
    }

    private long calculateSunlightDuration(Calendar sunrise, Calendar sunset) {
        long sunriseMillis = sunrise.getTimeInMillis();
        long sunsetMillis = sunset.getTimeInMillis();

        // Calculate the duration of sunlight in minutes
        return (sunsetMillis - sunriseMillis) / (60 * 1000);
    }

    private double getChargeValue(double chargeLimitPercentage, double currPower, double maxPower, double chargePower) {
        double chargeLimit = (chargeLimitPercentage / 100.0) * maxPower;
        double chargeValue;
        if (currPower + chargePower > chargeLimit) {
            double excess = currPower + chargePower - chargeLimit;
            chargeValue = chargePower - excess;
        } else {
            chargeValue = chargePower;
        }

        return chargeValue;
    }

    private double consumePowerFromBatteries(HomeBatteryEntity battery, long propertyId, double powerConsumed, Instant timestamp) {
        double excessPower = 0.0;

        Map<String, String> tags = new HashMap<>();
        tags.put(TAG_KEY_PROPERTY_ID, String.valueOf(propertyId));
        tags.put(HOME_BATTERY_TAG_KEY_CONSUMPTION_TYPE, HOME_BATTERY_TAG_VALUE_CONSUMED_CONSUMED);

        double currentPower = battery.getCurrent() - powerConsumed;
        if (currentPower < 0.0) {
            influxDBQueryService.save(HOME_BATTERY_DEVICE_NAME, HOME_BATTERY_FIELD_POWER_CONSUMPTION, battery.getCurrent(), timestamp, tags);
            battery.setCurrent(0.0);
            excessPower = Math.abs(currentPower);
        } else if (currentPower > battery.getCapacity()) {
            influxDBQueryService.save(HOME_BATTERY_DEVICE_NAME, HOME_BATTERY_FIELD_POWER_CONSUMPTION, battery.getCurrent() - battery.getCapacity(), timestamp, tags);
            battery.setCurrent(battery.getCapacity());
            excessPower = battery.getCapacity() - currentPower;
        } else {
            battery.setCurrent(currentPower);
            influxDBQueryService.save(HOME_BATTERY_DEVICE_NAME, HOME_BATTERY_FIELD_POWER_CONSUMPTION, powerConsumed, timestamp, tags);
        }
        homeBatteryRepository.save(battery);

        return excessPower;
    }

    // ### SPRINKLER SYSTEM ### //
    private void insertTestSprinklerSystems() {
        generateSprinklerSystems();
    }

    private void generateSprinklerSystems() {

        SprinklerSystemScheduleEntity schedule1 = SprinklerSystemScheduleEntity.builder()
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("10:30:00"))
                .days(List.of(DayOfWeek.TUESDAY))
                .build();

        SprinklerSystemScheduleEntity schedule2 = SprinklerSystemScheduleEntity.builder()
                .startTime(LocalTime.parse("17:00:00"))
                .endTime(LocalTime.parse("17:30:00"))
                .days(List.of(DayOfWeek.TUESDAY, DayOfWeek.SATURDAY))
                .build();

        IntStream.range(0, 2)
                .parallel()
                .forEach(i -> {
                    List<SprinklerSystemScheduleEntity> schedule;
                    if (i == 0) schedule = List.of(schedule1);
                    else schedule = List.of(schedule2);
                    SprinklerSystemEntity sprinklerSystem = sprinklerSystemRepository.save(SprinklerSystemEntity.builder()
                            .name("Sprinkler System " + i)
                            .property(TEST_PROPERTY)
                            .deviceType(SPRINKLER_SYSTEM)
                            .deviceActive(true)
                            .powerConsumption(204.7)
                            .groupType(DeviceGroupType.SPU)
                            .systemOn(false)
                            .schedule(schedule)
                            .build());

                    if (i == 0) SPRINKLER_SYSTEM_1 = sprinklerSystem;
                    else SPRINKLER_SYSTEM_2 = sprinklerSystem;

                    SprinklerSystemInfluxDBDataGenerator generator = new SprinklerSystemInfluxDBDataGenerator(sprinklerSystem, influxDBQueryService);
                    generator.generateData();
                });
    }

    // ### VEHICLE GATE ### //
    private void insertTestVehicleGates() {
        generateVehicleGates();
    }

    private void generateVehicleGates() {
        IntStream.range(0, 2)
                .parallel()
                .forEach(i -> {
                    List<String> allowedVehiclePlates;
                    if (i == 0) allowedVehiclePlates = List.of("AAA-1-AAA");
                    else allowedVehiclePlates = List.of("BBB-2-BBB", "CCC-3-CCC");
                    VehicleGateEntity vehicleGate = vehicleGateRepository.save(VehicleGateEntity.builder()
                            .name("Vehicle Gate " + i)
                            .property(TEST_PROPERTY)
                            .deviceActive(true)
                            .deviceType(VEHICLE_GATE)
                            .powerConsumption(293.2)
                            .groupType(DeviceGroupType.SPU)
                            .allowedLicencePlates(allowedVehiclePlates)
                            .build());

                    if (i == 0) VEHICLE_GATE_1 = vehicleGate;
                    else VEHICLE_GATE_2 = vehicleGate;

                    VehicleGateInfluxDBDataGenerator generator = new VehicleGateInfluxDBDataGenerator();
                    generator.setVehicleGate(vehicleGate);
                    generator.setInfluxDBQueryService(influxDBQueryService);
                    generator.generateData();
                });
    }

    // ### LAMP ### //
    private void insertTestLamps() {
        generateLamps();
    }

    private void generateLamps() {
        IntStream.range(0, 2)
                .parallel()
                .forEach(i -> {
                    LampEntity lamp = lampRepository.save(LampEntity.builder()
                            .name("Lamp " + i)
                            .property(TEST_PROPERTY)
                            .deviceActive(true)
                            .deviceType(LAMP)
                            .powerConsumption(39.5)
                            .groupType(DeviceGroupType.SPU)
                            .build());

                    if (i == 0) LAMP_1 = lamp;
                    else LAMP_2 = lamp;

                    LampInfluxDBDataGenerator generator = new LampInfluxDBDataGenerator();
                    generator.setLamp(lamp);
                    generator.setInfluxDBQueryService(influxDBQueryService);
                    generator.generateData();
                });
    }


}
