package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.StockProductDTO;
import com.hss.hss_backend.entity.StockProduct;
import com.hss.hss_backend.repository.StockProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockProductController {

  private final StockProductRepository stockProductRepository;

  private StockProductDTO mapToDTO(StockProduct product) {
    return StockProductDTO.builder()
        .id(product.getProductId())
        .name(product.getName())
        .barcode(product.getBarcode())
        .currentStock(product.getCurrentStock())
        .minStock(product.getMinStock())
        .category(product.getCategory() != null ? product.getCategory().name() : null)
        .location(product.getLocation())
        .sellingPrice(product.getSellingPrice() != null ? product.getSellingPrice().doubleValue() : null)
        .build();
  }

  @GetMapping
  public ResponseEntity<List<StockProductDTO>> getAllProducts() {
    return ResponseEntity.ok(stockProductRepository.findByIsActive(true).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<StockProductDTO> getProductById(@PathVariable Long id) {
    return stockProductRepository.findById(id)
        .map(this::mapToDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/alerts")
  public ResponseEntity<List<StockProductDTO>> getLowStockAlerts() {
    return ResponseEntity.ok(stockProductRepository.findLowStockProducts().stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/category/{category}")
  public ResponseEntity<List<StockProductDTO>> getByCategory(@PathVariable StockProduct.Category category) {
    return ResponseEntity.ok(stockProductRepository.findByCategory(category).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/expiring")
  public ResponseEntity<List<StockProductDTO>> getExpiringProducts(
      @RequestParam(defaultValue = "30") int days) {
    LocalDate expirationDate = LocalDate.now().plusDays(days);
    return ResponseEntity.ok(stockProductRepository.findExpiringProducts(expirationDate).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/barcode/{barcode}")
  public ResponseEntity<StockProductDTO> getByBarcode(@PathVariable String barcode) {
    return stockProductRepository.findByBarcode(barcode)
        .map(this::mapToDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/search")
  public ResponseEntity<List<StockProductDTO>> searchByName(@RequestParam String name) {
    return ResponseEntity.ok(stockProductRepository.findByNameContainingIgnoreCase(name).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }
}
