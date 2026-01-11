package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.response.OwnerFinancialSummaryResponse;
import com.hss.hss_backend.repository.InvoiceRepository;
import com.hss.hss_backend.repository.OwnerRepository;
import com.hss.hss_backend.repository.PaymentRepository;
import com.hss.hss_backend.service.impl.OwnerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

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

  @Test
  void getFinancialSummary_ShouldCalculateBalanceCorrectly() {
    Long ownerId = 1L;
    BigDecimal totalInvoiced = new BigDecimal("1000.00");
    BigDecimal totalPaid = new BigDecimal("400.00");
    BigDecimal overdue = new BigDecimal("200.00");

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
