package com.hss.hss_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    private long maxSize = 10485760L; // 10MB default

    private List<String> allowedTypes = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf",
            "text/plain");
}
