package nvt.project.smart_home.main.core.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ImageRequestDto {
    private String base64FormatString;
    private String name;
    private String format;
}
