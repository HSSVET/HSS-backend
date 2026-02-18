package com.hss.hss_backend.mapper;

import com.hss.hss_backend.dto.response.PaymentResponse;
import com.hss.hss_backend.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public PaymentResponse toResponse(Payment payment) {
    if (payment == null) {
      return null;
    }

    return PaymentResponse.builder()
        .paymentId(payment.getPaymentId())
        .ownerId(payment.getOwner() != null ? payment.getOwner().getOwnerId() : null)
        .invoiceId(payment.getInvoice() != null ? payment.getInvoice().getInvoiceId() : null)
        .amount(payment.getAmount())
        .paymentDate(payment.getPaymentDate())
        .paymentMethod(payment.getPaymentMethod())
        .notes(payment.getNotes())
        .build();
  }
}
