package com.hss.hss_backend.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.hss.hss_backend.service.IdentityUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final IdentityUserService identityUserService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestBody CreateUserRequest request) {
        try {
            UserRecord userRecord;
            if (request.getRole() != null) {
                userRecord = identityUserService.createUserWithRole(
                        request.getEmail(),
                        request.getPassword(),
                        request.getDisplayName(),
                        request.getRole());
            } else {
                userRecord = identityUserService.createUser(
                        request.getEmail(),
                        request.getPassword(),
                        request.getDisplayName());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("uid", userRecord.getUid());
            response.put("email", userRecord.getEmail());
            response.put("displayName", userRecord.getDisplayName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (FirebaseAuthException e) {
            log.error("Failed to create user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        try {
            UserRecord userRecord = identityUserService.getUserByEmail(email);
            Map<String, Object> response = new HashMap<>();
            response.put("uid", userRecord.getUid());
            response.put("email", userRecord.getEmail());
            response.put("displayName", userRecord.getDisplayName());
            response.put("disabled", userRecord.isDisabled());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            log.error("Failed to get user by email", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> getUserByUid(@PathVariable String uid) {
        try {
            UserRecord userRecord = identityUserService.getUserByUid(uid);
            Map<String, Object> response = new HashMap<>();
            response.put("uid", userRecord.getUid());
            response.put("email", userRecord.getEmail());
            response.put("displayName", userRecord.getDisplayName());
            response.put("disabled", userRecord.isDisabled());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            log.error("Failed to get user by uid", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable String uid,
            @RequestBody UpdateUserRequest request) {
        try {
            UserRecord userRecord = identityUserService.updateUser(
                    uid,
                    request.getEmail(),
                    request.getDisplayName());
            Map<String, Object> response = new HashMap<>();
            response.put("uid", userRecord.getUid());
            response.put("email", userRecord.getEmail());
            response.put("displayName", userRecord.getDisplayName());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            log.error("Failed to update user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{uid}/role")
    public ResponseEntity<Map<String, String>> setUserRole(
            @PathVariable String uid,
            @RequestBody SetRoleRequest request) {
        try {
            identityUserService.setRole(uid, request.getRole());
            return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
        } catch (FirebaseAuthException e) {
            log.error("Failed to set user role", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{uid}/disable")
    public ResponseEntity<Map<String, Object>> disableUser(@PathVariable String uid) {
        try {
            UserRecord userRecord = identityUserService.disableUser(uid);
            Map<String, Object> response = new HashMap<>();
            response.put("uid", userRecord.getUid());
            response.put("disabled", userRecord.isDisabled());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            log.error("Failed to disable user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{uid}/enable")
    public ResponseEntity<Map<String, Object>> enableUser(@PathVariable String uid) {
        try {
            UserRecord userRecord = identityUserService.enableUser(uid);
            Map<String, Object> response = new HashMap<>();
            response.put("uid", userRecord.getUid());
            response.put("disabled", userRecord.isDisabled());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            log.error("Failed to enable user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String uid) {
        try {
            identityUserService.deleteUser(uid);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (FirebaseAuthException e) {
            log.error("Failed to delete user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // DTO classes
    public static class CreateUserRequest {
        private String email;
        private String password;
        private String displayName;
        private String role;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class UpdateUserRequest {
        private String email;
        private String displayName;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    public static class SetRoleRequest {
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}

