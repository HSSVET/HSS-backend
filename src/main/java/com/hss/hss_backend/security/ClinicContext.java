package com.hss.hss_backend.security;

public class ClinicContext {
  private static final ThreadLocal<Long> currentClinicId = new ThreadLocal<>();

  public static void setClinicId(Long clinicId) {
    currentClinicId.set(clinicId);
  }

  public static Long getClinicId() {
    return currentClinicId.get();
  }

  public static void clear() {
    currentClinicId.remove();
  }
}
