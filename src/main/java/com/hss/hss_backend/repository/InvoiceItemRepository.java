package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoiceInvoiceId(Long invoiceId);

    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.invoice.invoiceId = :invoiceId")
    List<InvoiceItem> findByInvoiceId(@Param("invoiceId") Long invoiceId);

    List<InvoiceItem> findByItemType(InvoiceItem.ItemType itemType);
}

