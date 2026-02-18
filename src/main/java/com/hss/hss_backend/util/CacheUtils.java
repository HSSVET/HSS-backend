package com.hss.hss_backend.util;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for adding cache headers to HTTP responses
 * Optimized for React Query caching strategy
 */
public class CacheUtils {
    
    /**
     * Short cache for frequently changing data (30 seconds)
     * Good for: Recent activities, notifications
     */
    public static CacheControl shortCache() {
        return CacheControl.maxAge(30, TimeUnit.SECONDS)
                .cachePublic();
    }
    
    /**
     * Medium cache for semi-static data (5 minutes)
     * Good for: Entity lists, dashboard data
     */
    public static CacheControl mediumCache() {
        return CacheControl.maxAge(5, TimeUnit.MINUTES)
                .cachePublic();
    }
    
    /**
     * Long cache for static data (1 hour)
     * Good for: Reference data, species, breeds
     */
    public static CacheControl longCache() {
        return CacheControl.maxAge(1, TimeUnit.HOURS)
                .cachePublic();
    }
    
    /**
     * No cache for mutations and sensitive data
     * Good for: POST, PUT, DELETE, user-specific data
     */
    public static CacheControl noCache() {
        return CacheControl.noStore()
                .mustRevalidate();
    }
    
    /**
     * Generate simple ETag from object hashCode
     */
    public static String generateETag(Object data) {
        if (data == null) {
            return "\"empty\"";
        }
        return "\"" + Integer.toHexString(data.hashCode()) + "\"";
    }
    
    /**
     * Add pagination headers to response
     */
    public static <T> ResponseEntity.BodyBuilder addPaginationHeaders(
            ResponseEntity.BodyBuilder builder,
            int currentPage,
            int pageSize,
            long totalElements,
            int totalPages
    ) {
        return builder
                .header("X-Page-Number", String.valueOf(currentPage))
                .header("X-Page-Size", String.valueOf(pageSize))
                .header("X-Total-Count", String.valueOf(totalElements))
                .header("X-Total-Pages", String.valueOf(totalPages));
    }
}
