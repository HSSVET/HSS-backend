package com.hss.hss_backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Response wrapper for consistent response format
 * Compatible with frontend TypeScript ApiResponse<T> interface
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private String message;
    private String error;
    private Integer status;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * Create a successful response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .status(200)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create a successful response with data and custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .status(200)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(String error, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create an error response with default 500 status
     */
    public static <T> ApiResponse<T> error(String error) {
        return error(error, 500);
    }
}
