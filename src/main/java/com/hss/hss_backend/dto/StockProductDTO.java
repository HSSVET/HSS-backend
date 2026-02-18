package com.hss.hss_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockProductDTO {
  private Long id;
  private String name;
  private String barcode;
  private Integer currentStock;
  private Integer minStock;
  private String category;
  private String location;
  private Double sellingPrice;
}
