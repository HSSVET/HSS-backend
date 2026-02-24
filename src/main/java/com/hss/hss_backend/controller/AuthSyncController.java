package com.hss.hss_backend.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.hss.hss_backend.entity.UserAccount;
import com.hss.hss_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthSyncController {

  @Autowired
  private UserService userService;

  @Autowired(required = false)
  private FirebaseAuth firebaseAuth;

  @PostMapping("/sync")
  public ResponseEntity<Map<String, Object>> syncUser(Authentication authentication) {
    String firebaseUid = authentication.getName(); // Firebase UID
    
    // Get claims from authentication details if available
    String email = null;
    String name = null;
    String givenRole = null;
    
    // Try to extract from JWT if it's a Jwt type
    if (authentication.getPrincipal() instanceof Jwt) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      firebaseUid = jwt.getSubject();
      email = jwt.getClaimAsString("email");
      name = jwt.getClaimAsString("name");
      givenRole = jwt.getClaimAsString("role");
    } else {
      // If it's from Firebase (String principal), use the name as UID
      firebaseUid = (String) authentication.getPrincipal();
    }

    UserAccount user = userService.syncUser(firebaseUid, email, name, givenRole);

    Map<String, Object> response = new HashMap<>();
    response.put("userId", user.getUserId());
    response.put("firebaseUid", user.getFirebaseUid());
    response.put("username", user.getUsername());

    Map<String, Object> claims = new HashMap<>();
    // Preserve existing claims if needed, but usually we overwrite for authority

    if (user.getStaff() != null) {
      response.put("userType", "STAFF");
      response.put("staffId", user.getStaff().getStaffId()); // Added staffId
      claims.put("user_type", "STAFF");
      claims.put("staff_id", user.getStaff().getStaffId());
      if (user.getStaff().getClinic() != null) {
        Long clinicId = user.getStaff().getClinic().getClinicId();
        String clinicSlug = user.getStaff().getClinic().getSlug();
        response.put("clinicId", clinicId);
        response.put("clinicSlug", clinicSlug);
        claims.put("clinic_id", clinicId);
        claims.put("clinic_slug", clinicSlug);
      }
      // Add roles
      // Add roles
      java.util.List<String> roles = userService.getUserRoles(user.getUsername());
      response.put("roles", roles);
      claims.put("roles", roles);
    } else if (user.getOwner() != null) {
      response.put("userType", "OWNER");
      response.put("ownerId", user.getOwner().getOwnerId()); // Added ownerId
      claims.put("user_type", "OWNER");
      claims.put("owner_id", user.getOwner().getOwnerId());
      if (user.getOwner().getClinic() != null) {
        Long clinicId = user.getOwner().getClinic().getClinicId();
        String clinicSlug = user.getOwner().getClinic().getSlug();
        response.put("clinicId", clinicId);
        response.put("clinicSlug", clinicSlug);
        claims.put("clinic_id", clinicId);
        claims.put("clinic_slug", clinicSlug);
      }
    }

    if (firebaseAuth != null) {
      try {
        firebaseAuth.setCustomUserClaims(firebaseUid, claims);
        response.put("claimsUpdated", true);
      } catch (Exception e) {
        response.put("claimsUpdated", false);
        response.put("error", "Failed to set custom claims: " + e.getMessage());
      }
    } else {
      response.put("claimsUpdated", false);
      response.put("warning", "Firebase Auth disabled, claims not updated");
    }

    return ResponseEntity.ok(response);
  }
}
