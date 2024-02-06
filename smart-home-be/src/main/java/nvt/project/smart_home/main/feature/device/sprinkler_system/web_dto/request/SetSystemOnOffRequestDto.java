package nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetSystemOnOffRequestDto {
    private boolean systemOn;
    private String userEmail;
}
