package nvt.project.smart_home.main.core.service.interf;

import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.dto.response.ImageResponseDto;

import java.io.IOException;

public interface IImageService {


    ImageResponseDto readProfileImageFromFileSystem(String fileName, String format) throws IOException;

    ImageResponseDto readImageFromFileSystem(String filePath) throws IOException;

    ImageRequestDto readImageFromFileSystemInRequestDtoFormat(String filePath) throws IOException;

    void saveProfileImageToFileSystem(ImageRequestDto imageRequest) throws IOException;

    void saveImageToFileSystem(String directoryPath, ImageRequestDto imageRequest) throws IOException;

    void saveDeviceImageToFileSystem(ImageRequestDto imageRequestDto) throws IOException;

    ImageResponseDto readDeviceImageFromFileSystem(String fileName, String format) throws IOException;

    void savePropertyImageToFileSystem(ImageRequestDto imageRequestDto) throws IOException;

    ImageResponseDto readPropertyImageFromFileSystem(String fileName, String format) throws IOException;

}
