package com.hss.hss_backend.service;

import com.hss.hss_backend.entity.Clinic;

public interface LicenseService {
  /**
   * Generates a semantic license key for the given plan type.
   * Format: HSS-YYYY-CODE-XXXX
   * 
   * @param info Info needed for generation (plan type, etc.)
   * @return Unique License Key String
   */
  String generateLicenseKey(String planType);

  /**
   * Calculates the end date for a license based on type.
   * 
   * @param planType  Plan type (e.g. STRT, PRO)
   * @param startDate Start date
   * @return End date
   */
  java.time.LocalDate calculateEndDate(String planType, java.time.LocalDate startDate);
}
