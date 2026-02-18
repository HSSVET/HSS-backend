package com.hss.hss_backend.controller;

import com.hss.hss_backend.dto.StockProductDTO;
import com.hss.hss_backend.entity.StockProduct;
import com.hss.hss_backend.service.StockProductService;
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

  private final StockProductService stockProductService;

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
    return ResponseEntity.ok(stockProductService.getAllActiveProducts().stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<StockProductDTO> getProductById(@PathVariable Long id) {
    try {
      StockProduct product = stockProductService.getProductById(id);
      return ResponseEntity.ok(mapToDTO(product));
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/alerts")
  public ResponseEntity<List<StockProductDTO>> getLowStockAlerts() {
    return ResponseEntity.ok(stockProductService.getLowStockProducts().stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/category/{category}")
  public ResponseEntity<List<StockProductDTO>> getByCategory(@PathVariable StockProduct.Category category) {
    return ResponseEntity.ok(stockProductService.findByCategory(category).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/expiring")
  public ResponseEntity<List<StockProductDTO>> getExpiringProducts(
      @RequestParam(defaultValue = "30") int days) {
    LocalDate expirationDate = LocalDate.now().plusDays(days);
    return ResponseEntity.ok(stockProductService.getExpiringProducts(expirationDate).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @GetMapping("/barcode/{barcode}")
  public ResponseEntity<StockProductDTO> getByBarcode(@PathVariable String barcode) {
    return stockProductService.findByBarcode(barcode)
        .map(this::mapToDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/search")
  public ResponseEntity<List<StockProductDTO>> searchByName(@RequestParam String name) {
    return ResponseEntity.ok(stockProductService.searchByName(name).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList()));
  }

  @PostMapping
  public ResponseEntity<StockProductDTO> createProduct(@RequestBody StockProductDTO productDto) {
    StockProduct product = new StockProduct();
    product.setName(productDto.getName());
    product.setBarcode(productDto.getBarcode());
    product.setCurrentStock(productDto.getCurrentStock() != null ? productDto.getCurrentStock() : 0);
    product.setMinStock(productDto.getMinStock() != null ? productDto.getMinStock() : 0);
    try {
      if (productDto.getCategory() != null) {
        product.setCategory(StockProduct.Category.valueOf(productDto.getCategory()));
      }
    } catch (IllegalArgumentException e) {
      // ignore invalid category or handle better
    }
    product.setLocation(productDto.getLocation());
    product.setSellingPrice(
        productDto.getSellingPrice() != null ? java.math.BigDecimal.valueOf(productDto.getSellingPrice()) : null);
    product.setIsActive(true);

    StockProduct saved = stockProductService.saveProduct(product);
    return ResponseEntity.ok(mapToDTO(saved));
  }

  @GetMapping("/stats")
  public ResponseEntity<java.util.Map<String, Object>> getStats() {
    // Simple aggregate stats
    List<StockProduct> all = stockProductService.getAllActiveProducts();
    int totalProducts = all.size();
    int lowStock = stockProductService.getLowStockProducts().size();
    int expired = stockProductService.getExpiringProducts(LocalDate.now()).size();
    double totalValue = all.stream()
        .filter(p -> p.getUnitCost() != null)
        .mapToDouble(p -> p.getUnitCost().doubleValue() * p.getCurrentStock())
        .sum();

    java.util.Map<String, Object> stats = new java.util.HashMap<>();
    stats.put("totalProducts", totalProducts);
    stats.put("totalStockValue", totalValue);
    stats.put("lowStockAlerts", lowStock);
    stats.put("expiredProducts", expired);

    return ResponseEntity.ok(stats);
  }

  @GetMapping("/movements")
  public ResponseEntity<List<com.hss.hss_backend.entity.StockTransaction>> getStockMovements() {
    return ResponseEntity.ok(stockProductService.getLastTransactions());
  }

  @GetMapping("/settings")
  public ResponseEntity<java.util.Map<String, Object>> getSettings() {
    // Mock settings for now
    java.util.Map<String, Object> settings = new java.util.HashMap<>();
    settings.put("lowStockThreshold", 10);
    settings.put("autoOrderEnabled", false);
    return ResponseEntity.ok(settings);
  }
}
