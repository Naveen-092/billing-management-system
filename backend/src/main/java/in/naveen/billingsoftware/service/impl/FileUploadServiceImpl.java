package in.naveen.billingsoftware.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import in.naveen.billingsoftware.service.FileUploadService;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    public FileUploadServiceImpl(
            @Value("${CLOUDINARY_CLOUD_NAME}") String cloudName,
            @Value("${CLOUDINARY_API_KEY}") String apiKey,
            @Value("${CLOUDINARY_API_SECRET}") String apiSecret) {

        this.cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret
                )
        );
    }

    @Override
    public String uploadFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is empty"
            );
        }

        try {

            String publicId = UUID.randomUUID().toString();

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "billing-management"
                    )
            );

            return result.get("secure_url").toString();

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while uploading the file"
            );
        }
    }

    @Override
    public boolean deleteFile(String imgUrl) {

        if (imgUrl == null || imgUrl.isEmpty()) {
            return false;
        }

        try {

            String publicId = extractPublicId(imgUrl);

            Map<?, ?> result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

            return "ok".equals(result.get("result"));

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while deleting the file"
            );
        }
    }

    private String extractPublicId(String imgUrl) {

        String path = imgUrl.substring(
                imgUrl.indexOf("/upload/") + 8
        );

        // Remove Cloudinary version, e.g. v123456789/
        if (path.startsWith("v")) {
            int slashIndex = path.indexOf("/");

            if (slashIndex != -1) {
                path = path.substring(slashIndex + 1);
            }
        }

        // Remove file extension
        int extensionIndex = path.lastIndexOf(".");

        if (extensionIndex != -1) {
            path = path.substring(0, extensionIndex);
        }

        return path;
    }
}