package com.knowly.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for serving uploaded files.
 * 
 * Profile Picture Access Policy:
 * Profile pictures are visible to any logged-in Knowly member (public directory model).
 * This is appropriate for a peer-search platform where users need to discover and
 * connect with experts. No additional access restrictions are implemented.
 */
@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String uploadUri = uploadPath.toUri().toString();

        registry.addResourceHandler("/uploads/profile/**")
        .addResourceLocations(uploadUri)
        .setCachePeriod(3600);
    }
}
