package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.StockProductDTO;
import com.hss.hss_backend.dto.response.BarcodeScanResponse;
import com.hss.hss_backend.entity.StockProduct;
import com.hss.hss_backend.entity.StockTransaction;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.StockProductRepository;
import com.hss.hss_backend.repository.StockTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockProductService {

  private final StockProductRepository stockProductRepository;
  private final StockTransactionRepository stockTransactionRepository;

  public StockProduct getProductById(Long id) {
    return stockProductRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("StockProduct", id));
  }

  public List<StockProduct> getAllActiveProducts() {
    return stockProductRepository.findByIsActive(true);
  }

  public List<StockProduct> getLowStockProducts() {
    return stockProductRepository.findLowStockProducts();
  }

  public Optional<StockProduct> findByBarcode(String barcode) {
    return stockProductRepository.findByBarcode(barcode);
  }

  public List<StockProduct> findByCategory(StockProduct.Category category) {
    return stockProductRepository.findByCategory(category);
  }

  public List<StockProduct> searchByName(String name) {
    return stockProductRepository.findByNameContainingIgnoreCase(name);
  }

  public List<StockProduct> getExpiringProducts(java.time.LocalDate expirationDate) {
    return stockProductRepository.findExpiringProducts(expirationDate);
  }

  public void deductStock(Long productId, Integer quantity, String reason, String relatedEntity, Long relatedId) {
    log.info("Deducting {} from product {} for {}", quantity, productId, reason);
    StockProduct product = getProductById(productId);

    if (product.getCurrentStock() < quantity) {
      throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
    }

    product.setCurrentStock(product.getCurrentStock() - quantity);
    stockProductRepository.save(product);

    // Record transaction
    StockTransaction transaction = StockTransaction.builder()
        .stockProduct(product)
        .transactionDate(LocalDateTime.now())
        .quantity(-quantity)
        .type(StockTransaction.TransactionType.OUT)
        .notes(reason)
        .relatedEntity(relatedEntity)
        .relatedId(relatedId)
        .unitCost(product.getUnitCost())
        .totalCost(
            product.getUnitCost() != null ? product.getUnitCost().multiply(java.math.BigDecimal.valueOf(quantity))
                : java.math.BigDecimal.ZERO)
        .build();

    stockTransactionRepository.save(transaction);
  }

  public void addStock(Long productId, Integer quantity, String reason, String relatedEntity, Long relatedId) {
    log.info("Adding {} to product {} for {}", quantity, productId, reason);
    StockProduct product = getProductById(productId);

    product.setCurrentStock(product.getCurrentStock() + quantity);
    stockProductRepository.save(product);

    StockTransaction transaction = StockTransaction.builder()
        .stockProduct(product)
        .transactionDate(LocalDateTime.now())
        .quantity(quantity)
        .type(StockTransaction.TransactionType.IN)
        .notes(reason)
        .relatedEntity(relatedEntity)
        .relatedId(relatedId)
        .unitCost(product.getUnitCost())
        .totalCost(
            product.getUnitCost() != null ? product.getUnitCost().multiply(java.math.BigDecimal.valueOf(quantity))
                : java.math.BigDecimal.ZERO)
        .build();

    stockTransactionRepository.save(transaction);
  }

  public StockProduct saveProduct(StockProduct product) {
    return stockProductRepository.save(product);
  }

  public List<StockTransaction> getLastTransactions() {
    // Assuming repository has findAll or findTop...
    // For now returning all, or we could add a repository method for top 50
    return stockTransactionRepository.findAll();
  }

  /**
   * Scan a barcode and return product information with validation.
   * 
   * @param barcode The barcode to scan
   * @return BarcodeScanResponse with product info and validation status
   */
  public BarcodeScanResponse scanBarcode(String barcode) {
    log.info("Scanning barcode: {}", barcode);

    Optional<StockProduct> productOpt = stockProductRepository.findByBarcode(barcode);

    if (productOpt.isEmpty()) {
      log.warn("Barcode not found: {}", barcode);
      return BarcodeScanResponse.notFound(barcode);
    }

    StockProduct product = productOpt.get();

    // Check if product is active
    if (!Boolean.TRUE.equals(product.getIsActive())) {
      log.warn("Product is inactive: {}", barcode);
      return BarcodeScanResponse.builder()
          .productId(product.getProductId())
          .name(product.getName())
          .barcode(product.getBarcode())
          .isValid(false)
          .warningMessage("Ürün aktif değil!")
          .build();
    }

    // Check expiration date
    if (product.getExpirationDate() != null && product.getExpirationDate().isBefore(LocalDate.now())) {
      log.warn("Product expired: {} - Expiry: {}", barcode, product.getExpirationDate());
      return BarcodeScanResponse.builder()
          .productId(product.getProductId())
          .name(product.getName())
          .barcode(product.getBarcode())
          .lotNo(product.getLotNo())
          .expirationDate(product.getExpirationDate())
          .isValid(false)
          .isExpired(true)
          .warningMessage("Ürün son kullanma tarihi geçmiş! (" + product.getExpirationDate() + ")")
          .build();
    }

    // Check stock availability
    if (product.getCurrentStock() == null || product.getCurrentStock() <= 0) {
      log.warn("Product out of stock: {}", barcode);
      return BarcodeScanResponse.outOfStock(product.getProductId(), product.getName(), barcode);
    }

    // Check low stock warning
    boolean isLowStock = product.getMinStock() != null && product.getCurrentStock() <= product.getMinStock();

    String warningMessage = null;
    if (isLowStock) {
      warningMessage = "Düşük stok uyarısı! Mevcut: " + product.getCurrentStock() + ", Minimum: " + product.getMinStock();
    }

    // Product is valid
    return BarcodeScanResponse.builder()
        .productId(product.getProductId())
        .name(product.getName())
        .barcode(product.getBarcode())
        .lotNo(product.getLotNo())
        .serialNumber(product.getLotNo()) // Using lotNo as serialNumber for now
        .productionDate(product.getProductionDate())
        .expirationDate(product.getExpirationDate())
        .currentStock(product.getCurrentStock())
        .unitCost(product.getUnitCost())
        .sellingPrice(product.getSellingPrice())
        .category(product.getCategory() != null ? product.getCategory().name() : null)
        .supplier(product.getSupplier())
        .location(product.getLocation())
        .isActive(product.getIsActive())
        .isValid(true)
        .isExpired(false)
        .isLowStock(isLowStock)
        .warningMessage(warningMessage)
        .build();
  }
}
