package in.naveen.billingsoftware.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import in.naveen.billingsoftware.service.FileUploadService;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is empty"
            );
        }

        try {

            File directory = new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();

            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(
                        originalFilename.lastIndexOf(".")
                );
            }

            String filename = UUID.randomUUID().toString() + extension;

            Path filePath = Paths.get(uploadDir, filename);

            Files.write(filePath, file.getBytes());

            return "/uploads/" + filename;

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while uploading the file"
            );
        }
    }

    @Override
    public boolean deleteFile(String imgUrl) {

        try {

            if (imgUrl == null || imgUrl.isEmpty()) {
                return false;
            }

            String filename = imgUrl.substring(
                    imgUrl.lastIndexOf("/") + 1
            );

            Path filePath = Paths.get(uploadDir, filename);

            return Files.deleteIfExists(filePath);

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while deleting the file"
            );
        }
    }
}