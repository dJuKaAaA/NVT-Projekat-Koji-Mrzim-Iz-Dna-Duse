package nvt.project.smart_home.main.feature.property.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyStatusRequestDto {
    @NotNull private Long id;
    @NotNull private Boolean isApproved;
    private String denialReason;
}
