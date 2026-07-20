package com.knowly.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.knowly.enums.MessageType;
import com.knowly.exceptions.FileStorageException;
import com.knowly.exceptions.InvalidImageException;

@Service
public class FileStorageService {
	@Value("${file.upload-dir}")
	private String uploadDir;

	@Value("${chat.upload-dir}")
	private String chatUploadDir;

	private static final Map<MessageType, AllowedType> RULES = Map.of(
		MessageType.Image, new AllowedType(Set.of(".jpg",".jpeg",".png",".webp",".gif"), 10L * 1024 * 1024),
		MessageType.Video, new AllowedType(Set.of(".mp4",".webm",".mov"), 50L * 1024 * 1024),
		MessageType.Voice, new AllowedType(Set.of(".webm",".mp3",".wav",".m4a",".ogg"), 10L * 1024 * 1024),
		MessageType.File,  new AllowedType(Set.of(".pdf",".docx",".doc",".xlsx",".pptx",".zip",".txt"), 20L * 1024 * 1024)
	);

	private record AllowedType(Set<String> extensions, long maxBytes) {}

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

	public StoredAttachment storeChatAttachment(MultipartFile file, MessageType type) {
		AllowedType rule = RULES.get(type);
		if (rule == null) throw new InvalidImageException("Unsupported attachment type.");

		if (file.isEmpty()) throw new FileStorageException("File is empty.");
		if (file.getSize() > rule.maxBytes())
			throw new FileStorageException("File exceeds the " + (rule.maxBytes() / (1024*1024)) + "MB limit for this type.");

		String original = file.getOriginalFilename();
		String extension = (original != null && original.contains("."))
				? original.substring(original.lastIndexOf(".")).toLowerCase() : "";
		if (!rule.extensions().contains(extension))
			throw new FileStorageException("File type not allowed for " + type + " messages.");

		// Extra validation for images, same pattern as the existing store()
		if (type == MessageType.Image) {
			try {
				if (ImageIO.read(file.getInputStream()) == null)
					throw new InvalidImageException("File is not a valid image.");
			} catch (IOException e) {
				throw new InvalidImageException("Failed to read image file.");
			}
		}

		String fileName = UUID.randomUUID() + extension;
		Path chatRoot = Paths.get(chatUploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(chatRoot);
			Files.copy(file.getInputStream(), chatRoot.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FileStorageException("Failed to store attachment.", e);
		}

		return new StoredAttachment(fileName, original, file.getContentType(), file.getSize());
	}

	public record StoredAttachment(String storedName, String originalName, String mimeType, long size) {}
}
