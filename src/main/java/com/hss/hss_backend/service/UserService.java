package com.hss.hss_backend.service;

import com.hss.hss_backend.entity.Role;
import com.hss.hss_backend.entity.Staff;
import com.hss.hss_backend.entity.StaffRole;
import com.hss.hss_backend.entity.UserAccount;
import com.hss.hss_backend.repository.RoleRepository;
import com.hss.hss_backend.repository.StaffRepository;
import com.hss.hss_backend.repository.StaffRoleRepository;
import com.hss.hss_backend.repository.UserAccountRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import com.hss.hss_backend.entity.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StaffRoleRepository staffRoleRepository;

    /**
     * Otomatik olarak test kullanıcısı oluşturur
     */
    public UserAccount createTestUser(String username, String email, String fullName, String roleName,
            String firebaseUid) {
        // Önce Staff oluştur
        Staff staff = Staff.builder()
                .fullName(fullName)
                .email(email)
                .phone("+90 555 123 4567")
                .hireDate(LocalDate.now())
                .active(true)
                .position("Test Position")
                .department("IT")
                .salary(java.math.BigDecimal.valueOf(50000))
                .address("Test Address")
                .emergencyContactName("Emergency Contact")
                .emergencyContactPhone("+90 555 987 6543")
                // Test için clinic null olabilir veya varsayılan bir clinic atanmalı
                .build();

        staff = staffRepository.save(staff);

        // Role'ü bul veya oluştur
        Role role = findOrCreateRole(roleName);

        // UserAccount oluştur
        UserAccount userAccount = UserAccount.builder()
                .staff(staff)
                .username(username)
                .email(email)
                .isActive(true)
                .firebaseUid(firebaseUid)
                .loginAttempts(0)
                .build();

        userAccount = userAccountRepository.save(userAccount);

        // StaffRole oluştur ve kaydet
        StaffRole staffRole = StaffRole.builder()
                .staff(staff)
                .role(role)
                .assignedDate(LocalDate.now())
                .assignedBy("SYSTEM")
                .build();

        staffRoleRepository.save(staffRole);

        return userAccount;
    }

    /**
     * Role'ü bulur, yoksa oluşturur
     */
    private Role findOrCreateRole(String roleName) {
        Optional<Role> existingRole = roleRepository.findByName(roleName);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }

        // Yeni role oluştur
        Role newRole = Role.builder()
                .name(roleName)
                .description("Otomatik oluşturulan " + roleName + " rolü")
                .isSystemRole(false)
                .permissions(getDefaultPermissionsForRole(roleName))
                .build();

        return roleRepository.save(newRole);
    }

    /**
     * Role'e göre varsayılan izinleri döndürür
     */
    private List<String> getDefaultPermissionsForRole(String roleName) {
        switch (roleName.toUpperCase()) {
            case "SUPER_ADMIN":
                return Arrays.asList(
                        "MANAGE_CLINICS", "MANAGE_TENANTS", "MANAGE_SYSTEM_SETTINGS",
                        "READ_ANIMALS", "WRITE_ANIMALS", "DELETE_ANIMALS", // Optional: Full Access
                        "READ_APPOINTMENTS", "WRITE_APPOINTMENTS", "DELETE_APPOINTMENTS",
                        "READ_DASHBOARD", "READ_STAFF", "WRITE_STAFF", "DELETE_STAFF",
                        "READ_USERS", "WRITE_USERS", "DELETE_USERS");
            case "ADMIN":
                return Arrays.asList(
                        "READ_ANIMALS", "WRITE_ANIMALS", "DELETE_ANIMALS",
                        "READ_APPOINTMENTS", "WRITE_APPOINTMENTS", "DELETE_APPOINTMENTS",
                        "READ_MEDICAL_HISTORY", "WRITE_MEDICAL_HISTORY", "DELETE_MEDICAL_HISTORY",
                        "READ_LAB_TESTS", "WRITE_LAB_TESTS", "DELETE_LAB_TESTS",
                        "READ_PRESCRIPTIONS", "WRITE_PRESCRIPTIONS", "DELETE_PRESCRIPTIONS",
                        "READ_VACCINATIONS", "WRITE_VACCINATIONS", "DELETE_VACCINATIONS",
                        "READ_INVOICES", "WRITE_INVOICES", "DELETE_INVOICES",
                        "READ_INVENTORY", "WRITE_INVENTORY", "DELETE_INVENTORY",
                        "READ_FILES", "WRITE_FILES", "DELETE_FILES",
                        "READ_DASHBOARD", "READ_STAFF", "WRITE_STAFF", "DELETE_STAFF");
            case "VETERINARIAN":
                return Arrays.asList(
                        "READ_ANIMALS", "WRITE_ANIMALS",
                        "READ_APPOINTMENTS", "WRITE_APPOINTMENTS",
                        "READ_MEDICAL_HISTORY", "WRITE_MEDICAL_HISTORY",
                        "READ_LAB_TESTS", "WRITE_LAB_TESTS",
                        "READ_PRESCRIPTIONS", "WRITE_PRESCRIPTIONS",
                        "READ_VACCINATIONS", "WRITE_VACCINATIONS",
                        "READ_FILES", "WRITE_FILES",
                        "READ_DASHBOARD");
            case "STAFF":
                return Arrays.asList(
                        "READ_ANIMALS", "WRITE_ANIMALS",
                        "READ_APPOINTMENTS", "WRITE_APPOINTMENTS",
                        "READ_LAB_TESTS", "WRITE_LAB_TESTS",
                        "READ_VACCINATIONS", "WRITE_VACCINATIONS",
                        "READ_INVENTORY", "WRITE_INVENTORY",
                        "READ_FILES", "WRITE_FILES",
                        "READ_DASHBOARD");
            case "RECEPTIONIST":
                return Arrays.asList(
                        "READ_ANIMALS",
                        "READ_APPOINTMENTS", "WRITE_APPOINTMENTS",
                        "READ_INVOICES", "WRITE_INVOICES",
                        "READ_DASHBOARD");
            default:
                return Arrays.asList("READ_DASHBOARD");
        }
    }

    /**
     * Kullanıcı adına göre kullanıcıyı bulur
     */
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    /**
     * Email'e göre kullanıcıyı bulur
     */
    public Optional<UserAccount> findByEmail(String email) {
        return userAccountRepository.findByEmail(email);
    }

    /**
     * Kullanıcının rollerini döndürür
     */
    public List<String> getUserRoles(String username) {
        Optional<UserAccount> userAccount = userAccountRepository.findByUsername(username);
        if (userAccount.isPresent()) {
            List<StaffRole> staffRoles = staffRoleRepository
                    .findByStaffStaffId(userAccount.get().getStaff().getStaffId());
            return staffRoles.stream()
                    .map(staffRole -> staffRole.getRole().getName())
                    .toList();
        }
        return Arrays.asList();
    }

    private UserAccount initializeUserDependencies(UserAccount user) {
        if (user.getStaff() != null) {
            try {
                org.hibernate.Hibernate.initialize(user.getStaff());
                if (user.getStaff().getClinic() != null) {
                    org.hibernate.Hibernate.initialize(user.getStaff().getClinic());
                }
            } catch (jakarta.persistence.EntityNotFoundException e) {
                System.err.println("Warning: Staff reference not found for user " + user.getUserId());
                user.setStaff(null);
                userAccountRepository.save(user);
            }
        }
        if (user.getOwner() != null) {
            try {
                org.hibernate.Hibernate.initialize(user.getOwner());
                if (user.getOwner().getClinic() != null) {
                    org.hibernate.Hibernate.initialize(user.getOwner().getClinic());
                }
            } catch (jakarta.persistence.EntityNotFoundException e) {
                System.err.println("Warning: Owner reference not found for user " + user.getUserId()
                        + ". Removing invalid reference.");
                user.setOwner(null);
                userAccountRepository.save(user);
            }
        }
        return user;
    }

    /**
     * Finds or creates a user based on Firebase Token claims.
     */
    public UserAccount syncUser(String firebaseUid, String email, String name, String givenRole) {
        UserAccount userAccount = null;

        // 1. Check by Firebase UID
        Optional<UserAccount> existing = userAccountRepository.findByFirebaseUid(firebaseUid);
        if (existing.isPresent()) {
            userAccount = existing.get();
        } else {
            // 2. Check by Email (Legacy/Pre-created)
            Optional<UserAccount> byEmail = userAccountRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                UserAccount account = byEmail.get();
                account.setFirebaseUid(firebaseUid);
                userAccount = userAccountRepository.save(account);
            } else {
                // 3. Check if Staff exists by Email
                Optional<Staff> staff = staffRepository.findByEmail(email);
                if (staff.isPresent()) {
                    userAccount = createAccountForStaff(staff.get(), firebaseUid, email);
                } else {
                    // 4. Check if Owner exists by Email or Create New Owner
                    Optional<Owner> owner = ownerRepository.findByEmail(email);
                    Owner ownerEntity = owner.orElseGet(() -> createOwnerFromFirebase(email, name));
                    userAccount = createAccountForOwner(ownerEntity, firebaseUid, email);
                }
            }
        }

        return initializeUserDependencies(userAccount);
    }

    private Owner createOwnerFromFirebase(String email, String name) {
        String[] parts = (name != null ? name : "User").split(" ", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "User";

        // Create with a default clinic (assuming clinic ID 1 for now if no context)
        // In a real scenario, we might need a default clinic or different logic
        // But Owner entity requires a clinic.
        // Let's assume we can fetch the first clinic or a default one.
        // For now, we'll try to sync without explicitly setting clinic if builder
        // allows,
        // but entity has nullable=false.
        // If we really need a clinic, we should fetch it.
        // However, based on the current context, we just fix the lazy loading.

        Owner newOwner = Owner.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
        // NOTE: This might fail if Clinic is required and not set.
        // But the previous code didn't set it either, so I am preserving behavior
        // while solving the LazyInitializationException.
        return ownerRepository.save(newOwner);
    }

    private UserAccount createAccountForStaff(Staff staff, String firebaseUid, String email) {
        UserAccount account = UserAccount.builder()
                .staff(staff)
                .email(email)
                .firebaseUid(firebaseUid)
                .username(email) // Default username
                .isActive(true)
                .build();
        return userAccountRepository.save(account);
    }

    @Autowired(required = false)
    private com.google.firebase.auth.FirebaseAuth firebaseAuth;

    /**
     * Creates a new Clinic Admin user (Staff + UserAccount + Role + Firebase User)
     */
    public UserAccount createClinicAdmin(com.hss.hss_backend.entity.Clinic clinic, String email, String firstName,
            String lastName) {
        // 1. Create Staff
        Staff staff = Staff.builder()
                .clinic(clinic)
                .fullName(firstName + " " + lastName)
                .email(email)
                .phone(clinic.getPhone()) // Use clinic phone as default
                .hireDate(LocalDate.now())
                .active(true)
                .position("Clinic Manager")
                .department("Management")
                .salary(java.math.BigDecimal.ZERO) // Owner/Manager might not have salary in this table initially
                .build();

        staff = staffRepository.save(staff);

        // 2. Create Firebase User
        String firebaseUid;
        try {
            com.google.firebase.auth.UserRecord.CreateRequest request = new com.google.firebase.auth.UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(false)
                    .setPassword("Admin123!") // Default temporary password
                    .setDisplayName(firstName + " " + lastName)
                    .setDisabled(false);

            com.google.firebase.auth.UserRecord userRecord = firebaseAuth.createUser(request);
            firebaseUid = userRecord.getUid();
        } catch (com.google.firebase.auth.FirebaseAuthException e) {
            // If user already exists in Firebase, try to get them
            try {
                com.google.firebase.auth.UserRecord userRecord = firebaseAuth.getUserByEmail(email);
                firebaseUid = userRecord.getUid();
            } catch (com.google.firebase.auth.FirebaseAuthException ex) {
                throw new RuntimeException("Failed to create or fetch Firebase user: " + ex.getMessage());
            }
        }

        // 3. Create UserAccount
        Role adminRole = findOrCreateRole("ADMIN");

        UserAccount userAccount = UserAccount.builder()
                .staff(staff)
                .email(email)
                .username(email)
                .firebaseUid(firebaseUid)
                .isActive(true)
                .loginAttempts(0)
                .build();

        userAccount = userAccountRepository.save(userAccount);

        // 4. Assign Role
        StaffRole staffRole = StaffRole.builder()
                .staff(staff)
                .role(adminRole)
                .assignedDate(LocalDate.now())
                .assignedBy("SYSTEM")
                .build();

        staffRoleRepository.save(staffRole);

        return userAccount;
    }

    private UserAccount createAccountForOwner(Owner owner, String firebaseUid, String email) {
        UserAccount account = UserAccount.builder()
                .owner(owner)
                .email(email)
                .firebaseUid(firebaseUid)
                .username(email)
                .isActive(true)
                .build();
        return userAccountRepository.save(account);
    }
}
