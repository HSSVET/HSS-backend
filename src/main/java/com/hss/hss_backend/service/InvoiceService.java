package com.hss.hss_backend.service;

import com.hss.hss_backend.entity.Invoice;

public interface InvoiceService {
  Invoice createInvoiceForSurgery(Long surgeryId);

  Invoice createInvoiceForHospitalization(Long hospitalizationId);
}
