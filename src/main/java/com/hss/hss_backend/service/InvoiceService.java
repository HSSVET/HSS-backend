package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.InvoiceCreateRequest;
import com.hss.hss_backend.dto.request.ServiceCreateRequest;
import com.hss.hss_backend.dto.response.InvoiceResponse;
import com.hss.hss_backend.dto.response.PaymentResponse;
import com.hss.hss_backend.dto.response.ServiceResponse;
import com.hss.hss_backend.entity.Invoice;
import com.hss.hss_backend.entity.Payment;

import java.util.List;

public interface InvoiceService {
  // Existing methods
  Invoice createInvoiceForSurgery(Long surgeryId);
  Invoice createInvoiceForHospitalization(Long hospitalizationId);
  
  // New methods for billing endpoints
  List<InvoiceResponse> getAllInvoices();
  InvoiceResponse getInvoiceById(Long id);
  InvoiceResponse createInvoice(InvoiceCreateRequest request);
  
  List<PaymentResponse> getAllPayments();
  PaymentResponse createPayment(Long invoiceId, PaymentResponse paymentData);
  
  List<ServiceResponse> getAllServices();
  ServiceResponse createService(ServiceCreateRequest request);
}
