package com.hss.hss_backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreateRequest {
  
  @NotBlank(message = "Service name is required")
  @Size(max =200, message = "Service name must not exceed 200 characters")
  private String name;
  
  private String description;
  
  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", message = "Price must be positive")
  private BigDecimal price;
  
  private String category;
}
