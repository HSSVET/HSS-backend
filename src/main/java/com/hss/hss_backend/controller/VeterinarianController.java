package com.hss.hss_backend.controller;

import com.hss.hss_backend.repository.StaffRepository;
import com.hss.hss_backend.entity.Staff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/veterinarians")
@RequiredArgsConstructor
@Slf4j
public class VeterinarianController {

    private final StaffRepository staffRepository;

    @GetMapping("/basic")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VETERINARIAN') or hasRole('STAFF') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<Map<String, Object>>> getActiveVeterinarians() {
        log.info("Fetching active veterinarians");
        
        try {
            List<Staff> veterinarians = staffRepository.findActiveStaffByRole("VETERINARIAN");
            
            List<Map<String, Object>> response = veterinarians.stream()
                    .map(vet -> {
                        Map<String, Object> vetMap = new HashMap<>();
                        vetMap.put("id", vet.getStaffId());
                        vetMap.put("fullName", vet.getFullName());
                        vetMap.put("email", vet.getEmail());
                        vetMap.put("phone", vet.getPhone());
                        return vetMap;
                    })
                    .collect(Collectors.toList());
            
            log.info("Found {} active veterinarians", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching active veterinarians", e);
            return ResponseEntity.status(500).body(List.of());
        }
    }
}

