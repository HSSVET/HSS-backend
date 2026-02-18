package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.response.OwnerFinancialSummaryResponse;
import com.hss.hss_backend.entity.Owner;
import com.hss.hss_backend.repository.InvoiceRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import com.hss.hss_backend.repository.PaymentRepository;
import com.hss.hss_backend.security.ClinicContext;
import com.hss.hss_backend.service.impl.OwnerServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceFinancialTest {

  @Mock
  private OwnerRepository ownerRepository;

  @Mock
  private InvoiceRepository invoiceRepository;

  @Mock
  private PaymentRepository paymentRepository;

  @InjectMocks
  private OwnerServiceImpl ownerService;

  private static final Long CLINIC_ID = 1L;

  @BeforeEach
  void setClinicContext() {
    ClinicContext.setClinicId(CLINIC_ID);
  }

  @AfterEach
  void clearClinicContext() {
    ClinicContext.clear();
  }

  @Test
  void getFinancialSummary_ShouldCalculateBalanceCorrectly() {
    Long ownerId = 1L;
    Owner owner = new Owner();
    owner.setOwnerId(ownerId);
    BigDecimal totalInvoiced = new BigDecimal("1000.00");
    BigDecimal totalPaid = new BigDecimal("400.00");
    BigDecimal overdue = new BigDecimal("200.00");

    when(ownerRepository.findByOwnerIdAndClinicClinicId(ownerId, CLINIC_ID)).thenReturn(Optional.of(owner));
    when(invoiceRepository.getTotalAmountByOwnerId(ownerId)).thenReturn(totalInvoiced);
    when(paymentRepository.sumAmountByOwner_OwnerId(ownerId)).thenReturn(totalPaid);
    when(invoiceRepository.getOverdueAmountByOwnerId(ownerId)).thenReturn(overdue);

    OwnerFinancialSummaryResponse response = ownerService.getFinancialSummary(ownerId);

    assertEquals(ownerId, response.getOwnerId());
    assertEquals(totalInvoiced, response.getTotalInvoiced());
    assertEquals(totalPaid, response.getTotalPaid());
    assertEquals(new BigDecimal("600.00"), response.getBalance()); // 1000 - 400 = 600
    assertEquals(overdue, response.getOverdueAmount());
  }

  @Test
  void getFinancialSummary_ShouldHandleNullValues() {
    Long ownerId = 2L;
    Owner owner = new Owner();
    owner.setOwnerId(ownerId);

    when(ownerRepository.findByOwnerIdAndClinicClinicId(ownerId, CLINIC_ID)).thenReturn(Optional.of(owner));
    when(invoiceRepository.getTotalAmountByOwnerId(ownerId)).thenReturn(null);
    when(paymentRepository.sumAmountByOwner_OwnerId(ownerId)).thenReturn(null);
    when(invoiceRepository.getOverdueAmountByOwnerId(ownerId)).thenReturn(null);

    OwnerFinancialSummaryResponse response = ownerService.getFinancialSummary(ownerId);

    assertEquals(ownerId, response.getOwnerId());
    assertEquals(BigDecimal.ZERO, response.getTotalInvoiced());
    assertEquals(BigDecimal.ZERO, response.getTotalPaid());
    assertEquals(BigDecimal.ZERO, response.getBalance());
    assertEquals(BigDecimal.ZERO, response.getOverdueAmount());
  }
}
