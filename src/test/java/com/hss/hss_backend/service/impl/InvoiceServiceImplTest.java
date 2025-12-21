package com.hss.hss_backend.service.impl;

import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.repository.HospitalizationRepository;
import com.hss.hss_backend.repository.InvoiceRepository;
import com.hss.hss_backend.repository.SurgeryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

  @Mock
  private InvoiceRepository invoiceRepository;
  @Mock
  private SurgeryRepository surgeryRepository;
  @Mock
  private HospitalizationRepository hospitalizationRepository;

  @InjectMocks
  private InvoiceServiceImpl invoiceService;

  @Test
  void createInvoiceForSurgery_ShouldCalculateTotalCorrectly() {
    Long surgeryId = 1L;
    Owner owner = new Owner();
    Animal animal = new Animal();
    animal.setName("Buddy");
    animal.setOwner(owner);

    Surgery surgery = new Surgery();
    surgery.setSurgeryId(surgeryId);
    surgery.setAnimal(animal);
    surgery.setStatus("COMPLETED");

    SurgeryMedication med = new SurgeryMedication();
    med.setMedicineId(101L);
    med.setQuantity(2);
    surgery.getMedications().add(med);

    when(surgeryRepository.findById(surgeryId)).thenReturn(Optional.of(surgery));
    when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Invoice result = invoiceService.createInvoiceForSurgery(surgeryId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getInvoiceItems().size()); // 1 Surgery + 1 Med

    // Base surgery 1000 + Meds (2 * 50) = 1100
    BigDecimal expectedTotal = new BigDecimal("1100.00");
    assertEquals(expectedTotal, result.getTotalAmount());
  }
}
