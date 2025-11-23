package com.linka.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadService {

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public List<String> uploadImages(List<MultipartFile> files, String listingId) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        
        if (files == null || files.isEmpty()) {
            return imageUrls;
        }

        // Create listing-specific directory
        Path listingDir = Paths.get(uploadDir, "listings", listingId);
        Files.createDirectories(listingDir);

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            
            if (file.isEmpty()) {
                continue;
            }

            // Validate file
            validateFile(file);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + "." + extension;
            
            // Save file
            Path targetPath = listingDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Create URL path
            String imageUrl = "/api/uploads/listings/" + listingId + "/" + filename;
            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }

    public void deleteListingImages(String listingId) {
        try {
            Path listingDir = Paths.get(uploadDir, "listings", listingId);
            if (Files.exists(listingDir)) {
                Files.walk(listingDir)
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete file: " + path);
                        }
                    });
                Files.deleteIfExists(listingDir);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete listing images directory: " + listingId);
        }
    }

    private void validateFile(MultipartFile file) throws IOException {
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum allowed size of 5MB");
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IOException("Invalid file name");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IOException("File type not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Invalid file type. Only image files are allowed");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    public String uploadSingleImage(MultipartFile file, String subDirectory) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        validateFile(file);

        // Create directory
        Path uploadPath = Paths.get(uploadDir, subDirectory);
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "." + extension;
        
        // Save file
        Path targetPath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return URL
        return "/api/uploads/" + subDirectory + "/" + filename;
    }
}