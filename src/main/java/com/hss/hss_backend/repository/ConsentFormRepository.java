package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.ConsentForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentFormRepository extends JpaRepository<ConsentForm, Long> {

  // Find consent forms by surgery
  List<ConsentForm> findBySurgerySurgeryId(Long surgeryId);

  // Find consent forms by animal
  List<ConsentForm> findByAnimalAnimalIdOrderByCreatedAtDesc(Long animalId);

  // Find consent forms by owner
  List<ConsentForm> findByOwnerOwnerIdOrderByCreatedAtDesc(Long ownerId);

  // Find by surgery and type
  List<ConsentForm> findBySurgerySurgeryIdAndFormType(Long surgeryId, ConsentForm.FormType formType);

  // Find by appointment
  List<ConsentForm> findByAppointmentAppointmentId(Long appointmentId);

  // Find pending forms
  List<ConsentForm> findByStatusAndClinicClinicId(ConsentForm.ConsentStatus status, Long clinicId);
}
