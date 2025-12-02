package com.cinema.storage.controller;

import com.cinema.shared.dto.ApiResponse;
import com.cinema.storage.dto.FileUploadResponse;
import com.cinema.storage.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "File upload and management APIs")
public class FileController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "uploads") String folder) {
        FileUploadResponse response = storageService.uploadFile(file, folder);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an image file")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "images") String folder) {
        FileUploadResponse response = storageService.uploadImage(file, folder);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/upload/movie-poster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a movie poster")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadMoviePoster(
            @RequestParam("file") MultipartFile file) {
        FileUploadResponse response = storageService.uploadImage(file, "movies/posters");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{fileName}")
    @Operation(summary = "Delete a file")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String fileName) {
        storageService.deleteFile(fileName);
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully"));
    }
}
