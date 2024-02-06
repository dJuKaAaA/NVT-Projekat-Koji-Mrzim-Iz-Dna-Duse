package nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetScheduleRequestDto {
    private long id;
    private List<SprinklerSystemScheduleWebRequestDto> schedule;
}
