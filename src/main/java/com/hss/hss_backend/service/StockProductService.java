package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.StockProductDTO;
import com.hss.hss_backend.entity.StockProduct;
import com.hss.hss_backend.entity.StockTransaction;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.StockProductRepository;
import com.hss.hss_backend.repository.StockTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
