package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    // Basic query methods
    Optional<Staff> findByEmail(String email);
    
    List<Staff> findByActive(Boolean active);
    
    List<Staff> findByDepartment(String department);
    
    List<Staff> findByPosition(String position);
    
    List<Staff> findByFullNameContainingIgnoreCase(String fullName);
    
    // Date range queries
    List<Staff> findByHireDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Staff> findByHireDateAfter(LocalDate date);
    
    List<Staff> findByHireDateBefore(LocalDate date);
    
    List<Staff> findByTerminationDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Custom queries with @Query
    @Query("SELECT s FROM Staff s WHERE s.email LIKE %:email%")
    List<Staff> findByEmailContaining(@Param("email") String email);
    
    @Query("SELECT s FROM Staff s WHERE s.fullName LIKE %:name%")
    List<Staff> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT s FROM Staff s WHERE s.department = :department AND s.active = true")
    List<Staff> findActiveStaffByDepartment(@Param("department") String department);
    
    @Query("SELECT s FROM Staff s WHERE s.position = :position AND s.active = true")
    List<Staff> findActiveStaffByPosition(@Param("position") String position);
    
    @Query("SELECT s FROM Staff s WHERE s.terminationDate IS NULL AND s.active = true")
    List<Staff> findCurrentlyActiveStaff();
    
    @Query("SELECT s FROM Staff s WHERE s.terminationDate IS NOT NULL")
    List<Staff> findTerminatedStaff();
    
    // Statistics queries
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.active = true")
    Long countActiveStaff();
    
    @Query("SELECT s.department, COUNT(s) FROM Staff s WHERE s.active = true GROUP BY s.department")
    List<Object[]> getStaffCountByDepartment();
    
    @Query("SELECT s.position, COUNT(s) FROM Staff s WHERE s.active = true GROUP BY s.position")
    List<Object[]> getStaffCountByPosition();
    
    // Find active staff by role
    @Query("SELECT DISTINCT s FROM Staff s " +
           "JOIN s.staffRoles sr " +
           "JOIN sr.role r " +
           "WHERE s.terminationDate IS NULL AND s.active = true AND r.name = :roleName")
    List<Staff> findActiveStaffByRole(@Param("roleName") String roleName);
    
    // Pagination support
    Page<Staff> findByActive(Boolean active, Pageable pageable);
    
    Page<Staff> findByDepartment(String department, Pageable pageable);
    
    Page<Staff> findByPosition(String position, Pageable pageable);
}