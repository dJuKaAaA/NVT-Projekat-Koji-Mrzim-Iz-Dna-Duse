package nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirCWorkAppointmentWebResponseDto {

    private long id;
    private String executor;
    private String startTime;
    private String endTime;
    private String command;
    private double wantedTemperature;
}
