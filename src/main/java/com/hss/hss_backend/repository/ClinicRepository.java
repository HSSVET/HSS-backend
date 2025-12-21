package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
  Optional<Clinic> findByName(String name);
}
