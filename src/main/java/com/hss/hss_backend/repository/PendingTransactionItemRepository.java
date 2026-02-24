package com.hss.hss_backend.repository;

import com.hss.hss_backend.entity.PendingTransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingTransactionItemRepository extends JpaRepository<PendingTransactionItem, Long> {

    List<PendingTransactionItem> findByPendingTransactionPendingTransactionId(Long transactionId);

    List<PendingTransactionItem> findByServiceTypeAndServiceId(PendingTransactionItem.ServiceType serviceType, Long serviceId);

    void deleteByPendingTransactionPendingTransactionId(Long transactionId);
}
