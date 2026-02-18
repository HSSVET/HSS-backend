package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
  List<StockTransaction> findByStockProductProductId(Long productId);
}
