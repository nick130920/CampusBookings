package edu.usco.campusbookings.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for handling image storage operations.
 * Manages upload, retrieval, and deletion of scenario images.
 */
@Service
@Slf4j
public class ImageStorageService {
    
    @Value("${app.upload.dir:uploads/escenarios/}")
    private String uploadDir;

    /**
     * Stores an uploaded image file.
     * 
     * @param file the multipart file to store
     * @return the URL path to the stored image
     * @throws IOException if there's an error storing the file
     */
    public String storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            log.warn("Attempted to store null or empty file");
            return null;
        }

        // Generate unique filename
        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (extension != null ? "." + extension : "");
        
        // Ensure upload directory exists
        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            log.info("Created upload directory: {}", dirPath.toAbsolutePath());
        }

        // Store the file
        Path filePath = dirPath.resolve(filename);
        file.transferTo(filePath);
        
        String imageUrl = "/" + uploadDir + filename;
        log.info("Image stored successfully: {} -> {}", file.getOriginalFilename(), imageUrl);
        
        return imageUrl;
    }

    /**
     * Deletes an image file by filename.
     * 
     * @param filename the name of the file to delete (without path)
     * @return true if the file was deleted, false if it didn't exist
     * @throws IOException if there's an error deleting the file
     */
    public boolean deleteImage(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            log.warn("Attempted to delete image with null or empty filename");
            return false;
        }

        // Clean the filename (remove any path components for security)
        String cleanFilename = Paths.get(filename).getFileName().toString();
        Path filePath = Paths.get(uploadDir, cleanFilename);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Image deleted successfully: {}", filePath.toAbsolutePath());
            return true;
        } else {
            log.warn("Attempted to delete non-existent image: {}", filePath.toAbsolutePath());
            return false;
        }
    }

    /**
     * Checks if an image file exists.
     * 
     * @param filename the name of the file to check
     * @return true if the file exists, false otherwise
     */
    public boolean imageExists(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        String cleanFilename = Paths.get(filename).getFileName().toString();
        Path filePath = Paths.get(uploadDir, cleanFilename);
        return Files.exists(filePath);
    }

    /**
     * Gets the file path for an image.
     * 
     * @param filename the image filename
     * @return the full file path
     */
    public Path getImagePath(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }

        String cleanFilename = Paths.get(filename).getFileName().toString();
        return Paths.get(uploadDir, cleanFilename);
    }

    /**
     * Extracts the file extension from a filename.
     * 
     * @param filename the filename to extract extension from
     * @return the file extension without the dot, or null if no extension
     */
    private String getExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        }
        return null;
    }

    /**
     * Generates a unique filename with the given extension.
     * 
     * @param originalFilename the original filename to preserve extension
     * @return a unique filename
     */
    public String generateUniqueFilename(String originalFilename) {
        String extension = getExtension(originalFilename);
        return UUID.randomUUID() + (extension != null ? "." + extension : "");
    }
}
