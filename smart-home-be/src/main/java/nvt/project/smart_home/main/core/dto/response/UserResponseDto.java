package nvt.project.smart_home.main.core.dto.response;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private ImageResponseDto profileImage;

}
