package com.hss.hss_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration for CORS and other web-related settings
 * Optimized for React Query frontend compatibility
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            // Development
            .allowedOrigins(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://127.0.0.1:3000"
            )
            // Production (update with your domain)
            .allowedOriginPatterns(
                "https://*.run.app",  // Google Cloud Run
                "https://yourdomain.com"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            // IMPORTANT: Expose cache headers for React Query
            .exposedHeaders(
                "Cache-Control",
                "ETag",
                "Last-Modified",
                "X-Total-Count",  // For pagination
                "X-Page-Number",
                "X-Page-Size",
                "X-Total-Pages"
            )
            .maxAge(3600); // Preflight cache: 1 hour
    }
}
