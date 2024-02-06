package nvt.project.smart_home.main.core.service.impl;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.dto.response.ImageResponseDto;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@RequiredArgsConstructor
@Service
public class DefaultImageService implements IImageService {
    @Value("${directory.path.profile.images}")
    private final String directoryPathOfProfileImage;
    @Value("${directory.path.property.images}")
    private final String directoryPathOfPropertyImage;
    @Value("${directory.path.device.images}")
    private final String directoryPathOfDeviceImage;
    @Value("${file.separator}")
    private final String fileSeparator;

    @Override
    public ImageResponseDto readProfileImageFromFileSystem(String fileName, String format)  {
        String imagePath = directoryPathOfProfileImage + fileSeparator + fileName + "." + format;
        return this.readImageFromFileSystem(imagePath);

    }

    @Override
    public ImageResponseDto readImageFromFileSystem(String filePath) {
        Path path = Path.of(filePath);
        return ImageResponseDto.builder()
                .name(getFileName(path))
                .format(getFileExtension(path))
                .build();
    }

    @Override
    public ImageRequestDto readImageFromFileSystemInRequestDtoFormat(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] binaryImage = Files.readAllBytes(path);
        return ImageRequestDto.builder().
                name(getFileName(path))
                .format(getFileExtension(path))
                .base64FormatString(byteArrayToBase64String(binaryImage))
                .build();
    }


    @Override
    public void saveProfileImageToFileSystem(ImageRequestDto imageRequest) throws IOException {
        this.saveImageToFileSystem(directoryPathOfProfileImage, imageRequest);
    }

    @Override
    public void saveImageToFileSystem(String directoryPath, ImageRequestDto imageRequest) throws IOException {
        createDirectoryIfNotExist(directoryPath);
        Path path = Path.of(directoryPath + fileSeparator + imageRequest.getName() + "." + imageRequest.getFormat());
        byte[] binaryImage = base64StringToByteArray(imageRequest.getBase64FormatString());
        Files.write(path, binaryImage);
    }

    @Override
    public void savePropertyImageToFileSystem(ImageRequestDto imageRequestDto) throws IOException {
        saveImageToFileSystem(directoryPathOfPropertyImage, imageRequestDto);
    }

    @Override
    public ImageResponseDto readPropertyImageFromFileSystem(String fileName, String format) throws IOException {
        String imagePath = directoryPathOfPropertyImage + fileSeparator + fileName + "." + format;
        return this.readImageFromFileSystem(imagePath);
    }

    @Override
    public void saveDeviceImageToFileSystem(ImageRequestDto imagePojo) throws IOException {
        saveImageToFileSystem(directoryPathOfDeviceImage, imagePojo);

    }

    @Override
    public ImageResponseDto readDeviceImageFromFileSystem(String fileName, String format) throws IOException {
        String imagePath = directoryPathOfDeviceImage + fileSeparator + fileName + "." + format;
        return this.readImageFromFileSystem(imagePath);
    }

    private byte[] base64StringToByteArray(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    private String byteArrayToBase64String(byte[] byteArray) {
        return Base64.getEncoder().encodeToString(byteArray);
    }

    private void createDirectoryIfNotExist(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Folder is created: " + directoryPath);
            } else {
                System.err.println("It's not possible to create folder: " + directoryPath);
            }
        }
    }

    private String getFileName(Path path) {
        return path.getFileName().toString();
    }

    private String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf(".");
        String fileExtension = "";
        if (lastDotIndex > 0) {
            fileExtension = fileName.substring(lastDotIndex + 1);
        }
        return fileExtension;
    }

}
