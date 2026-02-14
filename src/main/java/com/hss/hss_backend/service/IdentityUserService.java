package com.hss.hss_backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityUserService {

    private final FirebaseAuth firebaseAuth;

    /**
     * Create a new user in Firebase/Identity Platform
     */
    public UserRecord createUser(String email, String password, String displayName) throws FirebaseAuthException {
        CreateRequest request = new CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(displayName)
                .setEmailVerified(false);

        UserRecord userRecord = firebaseAuth.createUser(request);
        log.info("Created user in Identity Platform: {}", userRecord.getUid());
        return userRecord;
    }

    /**
     * Create a new user with role claim
     */
    public UserRecord createUserWithRole(String email, String password, String displayName, String role)
            throws FirebaseAuthException {
        UserRecord userRecord = createUser(email, password, displayName);
        setCustomUserClaims(userRecord.getUid(), Map.of("role", role));
        return userRecord;
    }

    /**
     * Update user information
     */
    public UserRecord updateUser(String uid, String email, String displayName) throws FirebaseAuthException {
        UpdateRequest request = new UpdateRequest(uid);
        if (email != null) {
            request.setEmail(email);
        }
        if (displayName != null) {
            request.setDisplayName(displayName);
        }

        UserRecord userRecord = firebaseAuth.updateUser(request);
        log.info("Updated user in Identity Platform: {}", uid);
        return userRecord;
    }

    /**
     * Get user by email
     */
    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return firebaseAuth.getUserByEmail(email);
    }

    /**
     * Get user by UID
     */
    public UserRecord getUserByUid(String uid) throws FirebaseAuthException {
        return firebaseAuth.getUser(uid);
    }

    /**
     * Delete user
     */
    public void deleteUser(String uid) throws FirebaseAuthException {
        firebaseAuth.deleteUser(uid);
        log.info("Deleted user from Identity Platform: {}", uid);
    }

    /**
     * Set custom claims for a user (e.g., role)
     * Note: Custom claims must be JSON-compatible values
     */
    public void setCustomUserClaims(String uid, Map<String, Object> claims) throws FirebaseAuthException {
        firebaseAuth.setCustomUserClaims(uid, claims);
        log.info("Set custom claims for user {}: {}", uid, claims);

        // Important: After setting custom claims, the user must re-authenticate
        // or their ID token must be refreshed to see the new claims
    }

    /**
     * Set role claim for a user
     */
    public void setRole(String uid, String role) throws FirebaseAuthException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        setCustomUserClaims(uid, claims);
    }

    /**
     * Disable user
     */
    public UserRecord disableUser(String uid) throws FirebaseAuthException {
        UpdateRequest request = new UpdateRequest(uid)
                .setDisabled(true);
        UserRecord userRecord = firebaseAuth.updateUser(request);
        log.info("Disabled user: {}", uid);
        return userRecord;
    }

    /**
     * Enable user
     */
    public UserRecord enableUser(String uid) throws FirebaseAuthException {
        UpdateRequest request = new UpdateRequest(uid)
                .setDisabled(false);
        UserRecord userRecord = firebaseAuth.updateUser(request);
        log.info("Enabled user: {}", uid);
        return userRecord;
    }
}

