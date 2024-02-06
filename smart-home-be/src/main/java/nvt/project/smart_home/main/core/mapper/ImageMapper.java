package nvt.project.smart_home.main.core.mapper;

import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.dto.response.ImageResponseDto;
import org.mapstruct.Mapper;

@Mapper
public interface ImageMapper {
    

    ImageRequestDto responseToRequest(ImageResponseDto image);

    ImageResponseDto requestToResponse(ImageRequestDto imageDto);
}
