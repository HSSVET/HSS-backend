package com.hss.hss_backend.aspect;

import com.hss.hss_backend.security.ClinicContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HibernateFilterAspect {

  @PersistenceContext
  private EntityManager entityManager;

  @Before("@annotation(org.springframework.transaction.annotation.Transactional) || @within(org.springframework.transaction.annotation.Transactional)")
  public void enableClinicFilter() {
    Long clinicId = ClinicContext.getClinicId();
    if (clinicId != null) {
      Session session = entityManager.unwrap(Session.class);
      session.enableFilter("clinicFilter").setParameter("clinicId", clinicId);

      // Set DB Session variable for RLS
      entityManager.createNativeQuery("SET LOCAL app.current_clinic_id = " + clinicId).executeUpdate();
    }
  }
}
