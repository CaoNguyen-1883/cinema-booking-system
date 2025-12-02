package com.cinema.storage.service;

import com.cinema.shared.exception.BusinessException;
import com.cinema.shared.exception.ErrorCode;
import com.cinema.storage.config.MinioProperties;
import com.cinema.storage.dto.FileUploadResponse;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/webm", "video/quicktime"
    );

    public FileUploadResponse uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename(), folder);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String url = getFileUrl(fileName);

            log.info("File uploaded successfully: {}", fileName);

            return FileUploadResponse.builder()
                    .fileName(fileName)
                    .originalName(file.getOriginalFilename())
                    .url(url)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();

        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to upload file");
        }
    }

    public FileUploadResponse uploadImage(MultipartFile file, String folder) {
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
                    "Invalid image type. Allowed: JPEG, PNG, GIF, WebP");
        }
        return uploadFile(file, folder);
    }

    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(fileName)
                            .build()
            );
            log.info("File deleted successfully: {}", fileName);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to delete file");
        }
    }

    public String getFileUrl(String fileName) {
        return properties.getEndpoint() + "/" + properties.getBucketName() + "/" + fileName;
    }

    public String getPresignedUrl(String fileName, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(fileName)
                            .method(Method.GET)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to get presigned URL: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to get file URL");
        }
    }

    public InputStream getFile(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to get file: {}", e.getMessage());
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "File not found");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "File is empty");
        }

        if (file.getSize() > properties.getMaxFileSize()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
                    "File size exceeds maximum limit of " + (properties.getMaxFileSize() / 1024 / 1024) + "MB");
        }
    }

    private String generateFileName(String originalName, String folder) {
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String uuid = UUID.randomUUID().toString();
        return folder + "/" + uuid + extension;
    }
}
