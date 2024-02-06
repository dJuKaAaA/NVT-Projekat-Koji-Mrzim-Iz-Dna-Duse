package nvt.project.smart_home.main.core.dto.response;

import lombok.*;

import java.util.Collection;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDto<T> {

    private int pageNumber;
    private int size;
    private Collection<T> content;

}
