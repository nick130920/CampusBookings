package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.usco.campusbookings.application.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing scenario images.
 * Handles image upload, retrieval, and deletion for scenarios.
 */
@RestController
@RequestMapping("/api/v1/escenarios/images")
@RequiredArgsConstructor
@Slf4j
public class EscenarioImageController {

    private final ImageStorageService imageStorageService;

    // Tipos de archivo permitidos
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    // Tamaño máximo: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * Upload a single image for a scenario.
     * 
     * @param file the image file to upload
     * @return the uploaded image URL and metadata
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
        summary = "Upload scenario image", 
        description = "Uploads a single image for a scenario. Supports JPEG, PNG, and WebP formats. Max size: 5MB.",
        responses = {
        }
    )
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validar archivo
            String validationError = validateFile(file);
            if (validationError != null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", validationError));
            }

            // Almacenar imagen
            String imageUrl = imageStorageService.storeImage(file);
            
            log.info("Image uploaded successfully: {}", imageUrl);
            
            ImageUploadResponse response = ImageUploadResponse.builder()
                .imageUrl(imageUrl)
                .originalName(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(file.getContentType())
                .success(true)
                .message("Imagen subida correctamente")
                .build();

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Error uploading image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error al subir la imagen",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Upload multiple images for a scenario.
     * 
     * @param files array of image files to upload
     * @return list of uploaded image URLs and metadata
     */
    @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
        summary = "Upload multiple scenario images", 
        description = "Uploads multiple images for a scenario. Supports JPEG, PNG, and WebP formats. Max size per file: 5MB.",
        responses = {
        }
    )
    public ResponseEntity<?> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        if (files.length == 0) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "No se proporcionaron archivos"));
        }

        if (files.length > 10) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Máximo 10 archivos permitidos"));
        }

        try {
            List<ImageUploadResponse> responses = Arrays.stream(files)
                .map(file -> {
                    try {
                        String validationError = validateFile(file);
                        if (validationError != null) {
                            return ImageUploadResponse.builder()
                                .originalName(file.getOriginalFilename())
                                .success(false)
                                .message(validationError)
                                .build();
                        }

                        String imageUrl = imageStorageService.storeImage(file);
                        return ImageUploadResponse.builder()
                            .imageUrl(imageUrl)
                            .originalName(file.getOriginalFilename())
                            .size(file.getSize())
                            .contentType(file.getContentType())
                            .success(true)
                            .message("Imagen subida correctamente")
                            .build();

                    } catch (IOException e) {
                        log.error("Error uploading file {}: {}", file.getOriginalFilename(), e.getMessage());
                        return ImageUploadResponse.builder()
                            .originalName(file.getOriginalFilename())
                            .success(false)
                            .message("Error al subir la imagen: " + e.getMessage())
                            .build();
                    }
                })
                .toList();

            log.info("Multiple images upload completed. Success: {}, Failed: {}", 
                responses.stream().mapToInt(r -> r.isSuccess() ? 1 : 0).sum(),
                responses.stream().mapToInt(r -> r.isSuccess() ? 0 : 1).sum());

            return ResponseEntity.ok(Map.of(
                "results", responses,
                "totalFiles", files.length,
                "successCount", responses.stream().mapToInt(r -> r.isSuccess() ? 1 : 0).sum(),
                "failedCount", responses.stream().mapToInt(r -> r.isSuccess() ? 0 : 1).sum()
            ));

        } catch (Exception e) {
            log.error("Error processing multiple image upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error al procesar las imágenes",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Delete an uploaded image.
     * 
     * @param filename the image filename to delete
     * @return confirmation of deletion
     */
    @DeleteMapping("/{filename}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
        summary = "Delete scenario image", 
        description = "Deletes an uploaded scenario image by filename",
        responses = {
        }
    )
    public ResponseEntity<?> deleteImage(@PathVariable String filename) {
        try {
            boolean deleted = imageStorageService.deleteImage(filename);
            
            if (deleted) {
                log.info("Image deleted successfully: {}", filename);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Imagen eliminada correctamente",
                    "filename", filename
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "error", "Imagen no encontrada",
                        "filename", filename
                    ));
            }

        } catch (Exception e) {
            log.error("Error deleting image {}: {}", filename, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error al eliminar la imagen",
                    "details", e.getMessage()
                ));
        }
    }

    /**
     * Get image upload constraints and allowed formats.
     * 
     * @return image upload configuration
     */
    @GetMapping("/config")
        summary = "Get image upload configuration", 
        description = "Returns the allowed file types, maximum size, and other upload constraints"
    )
    public ResponseEntity<?> getUploadConfig() {
        return ResponseEntity.ok(Map.of(
            "allowedFileTypes", ALLOWED_FILE_TYPES,
            "maxFileSize", MAX_FILE_SIZE,
            "maxFileSizeMB", MAX_FILE_SIZE / (1024 * 1024),
            "maxFilesPerUpload", 10,
            "allowedExtensions", Arrays.asList("jpg", "jpeg", "png", "webp")
        ));
    }

    /**
     * Validates the uploaded file.
     * 
     * @param file the file to validate
     * @return error message if validation fails, null if valid
     */
    private String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "El archivo está vacío";
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return String.format("El archivo es demasiado grande. Máximo permitido: %d MB", 
                MAX_FILE_SIZE / (1024 * 1024));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            return "Tipo de archivo no permitido. Formatos soportados: JPEG, PNG, WebP";
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return "Nombre de archivo inválido";
        }

        return null; // Valid file
    }

    /**
     * Response DTO for image upload operations.
     */
    public static class ImageUploadResponse {
        private String imageUrl;
        
        private String originalName;
        
        private Long size;
        
        private String contentType;
        
        private boolean success;
        
        private String message;

        // Builder pattern
        public static ImageUploadResponseBuilder builder() {
            return new ImageUploadResponseBuilder();
        }

        // Getters and setters
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        
        public Long getSize() { return size; }
        public void setSize(Long size) { this.size = size; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        // Builder class
        public static class ImageUploadResponseBuilder {
            private String imageUrl;
            private String originalName;
            private Long size;
            private String contentType;
            private boolean success;
            private String message;

            public ImageUploadResponseBuilder imageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
                return this;
            }

            public ImageUploadResponseBuilder originalName(String originalName) {
                this.originalName = originalName;
                return this;
            }

            public ImageUploadResponseBuilder size(Long size) {
                this.size = size;
                return this;
            }

            public ImageUploadResponseBuilder contentType(String contentType) {
                this.contentType = contentType;
                return this;
            }

            public ImageUploadResponseBuilder success(boolean success) {
                this.success = success;
                return this;
            }

            public ImageUploadResponseBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ImageUploadResponse build() {
                ImageUploadResponse response = new ImageUploadResponse();
                response.setImageUrl(this.imageUrl);
                response.setOriginalName(this.originalName);
                response.setSize(this.size);
                response.setContentType(this.contentType);
                response.setSuccess(this.success);
                response.setMessage(this.message);
                return response;
            }
        }
    }
}