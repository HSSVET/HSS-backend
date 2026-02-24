package com.hss.hss_backend.service.report;

import com.hss.hss_backend.repository.StockProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReportService {

    private final StockProductRepository stockProductRepository;

    public Map<String, Object> generateStatusReport() {
        log.info("Generating inventory status report");
        
        Map<String, Object> report = new HashMap<>();
        report.put("reportType", "INVENTORY_STATUS");
        report.put("generatedAt", LocalDate.now());
        
        var lowStockProducts = stockProductRepository.findLowStockProducts();
        var outOfStockProducts = stockProductRepository.findOutOfStockProducts();
        var expiringProducts = stockProductRepository.findExpiringProducts(LocalDate.now().plusDays(30));
        
        report.put("lowStockCount", lowStockProducts.size());
        report.put("outOfStockCount", outOfStockProducts.size());
        report.put("expiringSoonCount", expiringProducts.size());
        report.put("lowStockProducts", lowStockProducts);
        report.put("outOfStockProducts", outOfStockProducts);
        report.put("expiringProducts", expiringProducts);
        
        return report;
    }
}

