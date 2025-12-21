package com.hss.hss_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ClinicContextFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // Extract clinic_id from custom claims
        Long clinicId = null;
        Object clinicIdClaim = jwt.getClaim("clinic_id");

        if (clinicIdClaim instanceof Long) {
          clinicId = (Long) clinicIdClaim;
        } else if (clinicIdClaim instanceof Integer) {
          clinicId = ((Integer) clinicIdClaim).longValue();
        } else if (clinicIdClaim instanceof String) {
          try {
            clinicId = Long.parseLong((String) clinicIdClaim);
          } catch (NumberFormatException ignored) {
          }
        }

        if (clinicId != null) {
          ClinicContext.setClinicId(clinicId);
        }
      }

      filterChain.doFilter(request, response);
    } finally {
      ClinicContext.clear();
    }
  }
}
