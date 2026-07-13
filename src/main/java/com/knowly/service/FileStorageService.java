package com.knowly.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.knowly.exceptions.FileStorageException;
import com.knowly.exceptions.InvalidImageException;

@Service
public class FileStorageService {
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	private Path getUploadRoot() {
		return Paths.get(uploadDir).toAbsolutePath().normalize();
	}
	public String store(MultipartFile file){
		String contentType = file.getContentType();

		if(contentType == null || !contentType.startsWith("image/")){
		    throw new InvalidImageException("Only JPG, JPEG, PNG and WEBP images are allowed.");
		}
		if(file.getSize() > 5 * 1024 * 1024){
			throw new InvalidImageException("Profile picture must be smaller than 5 MB.");
		}
	        String original = file.getOriginalFilename();

	        String extension = "";
	        if(original != null && original.contains(".")){
	            extension = original.substring(original.lastIndexOf(".")).toLowerCase();
	        }
	        
	        // Validate extension against allowed list
	        Set<String> allowedExtensions = Set.of(".jpg", ".jpeg", ".png", ".webp");
	        if (!allowedExtensions.contains(extension)) {
	            throw new InvalidImageException("Only JPG, JPEG, PNG and WEBP images are allowed.");
	        }

	        // Validate actual image bytes
	        BufferedImage img;
	        try {
	            img = ImageIO.read(file.getInputStream());
	        } catch (IOException e) {
	            throw new InvalidImageException("Failed to read image file.");
	        }
	        if (img == null) {
	            throw new InvalidImageException("File is not a valid image.");
	        }
	        
	        String fileName = UUID.randomUUID() + extension;

	        Path path = getUploadRoot().resolve(fileName).normalize();
	        try {
	        Files.createDirectories(getUploadRoot());
	        
	        Files.copy(
	        	    file.getInputStream(),
	        	    path,
	        	    StandardCopyOption.REPLACE_EXISTING
	        	);
	        }
	        catch (IOException e) {
				throw new FileStorageException("Failed to store profile picture.",e);
			}

	        return fileName;
	}

	public void delete(String fileName){

	    if (fileName == null || fileName.isBlank()) {
	        return;
	    }

	    Path path = getUploadRoot().resolve(fileName).normalize();
	    if (!path.startsWith(getUploadRoot())) {
	        throw new FileStorageException("Invalid profile picture path.");
	    }
	    try {
	    Files.deleteIfExists(path);
	    }
	    catch(IOException e){
	    		throw new FileStorageException("Failed to store profile picture.",e);
	    }
	}
}
