package nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashingMachineWorkAppointmentWebResponseDto {
    private long id;
    private String executor;
    private String startTime;
    private String endTime;
    private String command;
}
