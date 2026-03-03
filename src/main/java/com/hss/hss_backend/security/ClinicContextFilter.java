package com.hss.hss_backend.security;

import com.hss.hss_backend.repository.ClinicRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves the current clinic from (in order):
 * 1. JWT claim "clinic_id"
 * 2. X-Clinic-Id request header
 * 3. X-Clinic-Slug request header
 * 4. Referer URL pattern /clinic/{slug}/...
 *
 * Registered in Security filter chain after BearerTokenAuthenticationFilter
 * (see SecurityConfig).
 */
@Slf4j
public class ClinicContextFilter extends OncePerRequestFilter {

  /** Matches /clinic/{slug}/ in Referer URLs. */
  private static final Pattern REFERER_SLUG_PATTERN = Pattern.compile("/clinic/([^/]+)");

  private final ClinicRepository clinicRepository;

  public ClinicContextFilter(ClinicRepository clinicRepository) {
    this.clinicRepository = clinicRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      Long clinicId = resolveClinicId(request);

      if (clinicId != null) {
        ClinicContext.setClinicId(clinicId);
        log.debug("Clinic context set to {} for {} {}", clinicId, request.getMethod(), request.getRequestURI());
      } else {
        log.warn("No clinic context resolved for {} {} – downstream may fail",
            request.getMethod(), request.getRequestURI());
      }

      filterChain.doFilter(request, response);
    } finally {
      ClinicContext.clear();
    }
  }

  // ---------- resolution chain ----------

  private Long resolveClinicId(HttpServletRequest request) {
    Long id;

    // 1. JWT claim
    id = fromJwt();
    if (id != null) {
      log.debug("Clinic resolved from JWT claim: {}", id);
      return id;
    }

    // 2. X-Clinic-Id header
    id = fromHeader(request, "X-Clinic-Id");
    if (id != null) {
      log.debug("Clinic resolved from X-Clinic-Id header: {}", id);
      return id;
    }

    // 3. X-Clinic-Slug header
    id = fromSlugHeader(request);
    if (id != null) {
      log.debug("Clinic resolved from X-Clinic-Slug header: {}", id);
      return id;
    }

    // 4. Subdomain (Host header)
    id = fromSubdomain(request);
    if (id != null) {
      log.debug("Clinic resolved from Subdomain: {}", id);
      return id;
    }

    // 5. Referer URL (/clinic/{slug}/...)
    id = fromReferer(request);
    if (id != null) {
      log.debug("Clinic resolved from Referer URL: {}", id);
      return id;
    }

    return null;
  }

  /** 1. Extract clinic_id from JWT claims. */
  private Long fromJwt() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
      return null;
    }

    Object claim = jwt.getClaim("clinic_id");
    if (claim instanceof Long l)
      return l;
    if (claim instanceof Integer i)
      return i.longValue();
    if (claim instanceof String s) {
      try {
        return Long.parseLong(s);
      } catch (NumberFormatException ignored) {
      }
    }
    return null;
  }

  /** 2. Parse a numeric header value. */
  private Long fromHeader(HttpServletRequest request, String headerName) {
    String value = request.getHeader(headerName);
    if (value == null || value.isBlank())
      return null;
    try {
      return Long.parseLong(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /** 3. Resolve clinic from X-Clinic-Slug header via DB lookup. */
  private Long fromSlugHeader(HttpServletRequest request) {
    String slug = request.getHeader("X-Clinic-Slug");
    if (slug == null || slug.isBlank())
      return null;
    return clinicRepository.findBySlug(slug.trim())
        .map(clinic -> clinic.getClinicId())
        .orElse(null);
  }

  /** 4. Resolve clinic from Subdomain. */
  private Long fromSubdomain(HttpServletRequest request) {
    String host = request.getHeader("Host");
    if (host == null || host.isBlank())
      return null;

    // e.g., hedef-vet.localhost:3000 or hedef-vet.hss.com
    String[] parts = host.split("\\.");
    if (parts.length > 0) {
      String slug = parts[0];
      // Ignore common non-clinic subdomains
      if (!slug.equalsIgnoreCase("www") && !slug.equalsIgnoreCase("admin") && !slug.equalsIgnoreCase("portal")
          && !slug.equalsIgnoreCase("localhost") && !slug.equalsIgnoreCase("127") && !slug.startsWith("192")) {
        return clinicRepository.findBySlug(slug)
            .map(clinic -> clinic.getClinicId())
            .orElse(null);
      }
    }
    return null;
  }

  /** 4. Parse /clinic/{slug}/ from the Referer header and resolve via DB. */
  private Long fromReferer(HttpServletRequest request) {
    String referer = request.getHeader("Referer");
    if (referer == null || referer.isBlank())
      return null;

    Matcher matcher = REFERER_SLUG_PATTERN.matcher(referer);
    if (!matcher.find())
      return null;

    String slug = matcher.group(1);
    return clinicRepository.findBySlug(slug)
        .map(clinic -> clinic.getClinicId())
        .orElse(null);
  }

}
