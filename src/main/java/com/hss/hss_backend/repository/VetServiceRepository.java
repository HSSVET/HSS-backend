package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.VetService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VetServiceRepository extends JpaRepository<VetService, Long> {
  List<VetService> findByCategory(String category);
  List<VetService> findByNameContainingIgnoreCase(String name);
}
