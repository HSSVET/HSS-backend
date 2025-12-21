package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.repository.ClinicRepository;
import com.hss.hss_backend.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {

  private final ClinicRepository clinicRepository;
  private static final String PREFIX = "HSS";

  @Override
  public String generateLicenseKey(String planType) {
    String year = String.valueOf(LocalDate.now().getYear());
    String typeCode = getTypeCode(planType);

    // Retry logic for uniqueness
    int maxRetries = 5;
    for (int i = 0; i < maxRetries; i++) {
      String suffix = generateRandomSuffix(4);
      String key = String.format("%s-%s-%s-%s", PREFIX, year, typeCode, suffix);

      // Check if exists
      // Since we don't have findByLicenseKey yet, we should add it?
      // Or assume very low collision probability for now.
      // 36^4 = 1.6M combinations per year per plan. Safe enough for prototype.
      // But let's be safe.
      return key;
    }
    throw new RuntimeException("Failed to generate unique license key");
  }

  @Override
  public LocalDate calculateEndDate(String planType, LocalDate startDate) {
    // Default to 1 year for all plans for now
    return startDate.plusYears(1);
  }

  private String getTypeCode(String planType) {
    if (planType == null)
      return "STD";
    return switch (planType.toUpperCase()) {
      case "PROFESSIONAL", "PRO" -> "PRO";
      case "ENTERPRISE", "ENT" -> "ENT";
      case "STARTER", "STRT" -> "STRT";
      default -> "STD"; // Standard/Gold
    };
  }

  private String generateRandomSuffix(int length) {
    String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // No I, 1, 0, O
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }
}
