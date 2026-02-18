package com.hss.hss_backend.service;

import com.hss.hss_backend.entity.*;
import com.hss.hss_backend.exception.ResourceNotFoundException;
import com.hss.hss_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing pending transactions (cart system).
 * Allows adding services to a pending balance that can be paid partially or fully.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PendingTransactionService {

    private final PendingTransactionRepository transactionRepository;
    private final PendingTransactionItemRepository itemRepository;
    private final OwnerRepository ownerRepository;
    private final AnimalRepository animalRepository;
    private final ClinicRepository clinicRepository;
    private final VetServiceRepository vetServiceRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Get or create a pending transaction for an owner.
     */
    public PendingTransaction getOrCreateForOwner(Long ownerId, Long clinicId) {
        List<PendingTransaction> existing = transactionRepository.findActivePendingByOwner(ownerId);
        
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerId));
        
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found: " + clinicId));

        PendingTransaction transaction = PendingTransaction.builder()
                .owner(owner)
                .clinic(clinic)
                .status(PendingTransaction.TransactionStatus.PENDING)
                .build();

        return transactionRepository.save(transaction);
    }

    /**
     * Get or create a pending transaction for an animal.
     */
    public PendingTransaction getOrCreateForAnimal(Long animalId, Long clinicId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found: " + animalId));

        List<PendingTransaction> existing = transactionRepository.findActivePendingByAnimal(animalId);
        
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found: " + clinicId));

        PendingTransaction transaction = PendingTransaction.builder()
                .owner(animal.getOwner())
                .animal(animal)
                .clinic(clinic)
                .status(PendingTransaction.TransactionStatus.PENDING)
                .build();

        return transactionRepository.save(transaction);
    }

    /**
     * Add a service item to a pending transaction.
     */
    public PendingTransactionItem addItem(Long transactionId, 
                                          PendingTransactionItem.ServiceType serviceType,
                                          Long serviceId,
                                          String description,
                                          BigDecimal unitPrice,
                                          Integer quantity,
                                          String notes) {
        PendingTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (transaction.getStatus() != PendingTransaction.TransactionStatus.PENDING &&
            transaction.getStatus() != PendingTransaction.TransactionStatus.PARTIAL_PAID) {
            throw new IllegalStateException("Cannot add items to a " + transaction.getStatus() + " transaction");
        }

        PendingTransactionItem item = PendingTransactionItem.builder()
                .pendingTransaction(transaction)
                .serviceType(serviceType)
                .serviceId(serviceId)
                .description(description)
                .unitPrice(unitPrice)
                .quantity(quantity != null ? quantity : 1)
                .notes(notes)
                .build();

        // Calculate line total
        item.setLineTotal(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));

        transaction.addItem(item);
        transactionRepository.save(transaction);

        log.info("Added item to transaction {}: {} - {}", transactionId, serviceType, description);
        return item;
    }

    /**
     * Add a service item using VetService entity.
     */
    public PendingTransactionItem addServiceItem(Long transactionId, Long vetServiceId, Integer quantity, String notes) {
        VetService service = vetServiceRepository.findById(vetServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + vetServiceId));

        PendingTransactionItem item = addItem(
                transactionId,
                mapServiceCategory(service.getCategory()),
                vetServiceId,
                service.getName(),
                service.getPrice(),
                quantity,
                notes
        );

        item.setVetService(service);
        return itemRepository.save(item);
    }

    /**
     * Remove an item from a pending transaction.
     */
    public void removeItem(Long transactionId, Long itemId) {
        PendingTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        PendingTransactionItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + itemId));

        if (!item.getPendingTransaction().getPendingTransactionId().equals(transactionId)) {
            throw new IllegalArgumentException("Item does not belong to this transaction");
        }

        transaction.removeItem(item);
        transactionRepository.save(transaction);

        log.info("Removed item {} from transaction {}", itemId, transactionId);
    }

    /**
     * Record a partial or full payment.
     */
    public PendingTransaction recordPayment(Long transactionId, BigDecimal amount, String paymentMethod, String notes) {
        PendingTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (transaction.getStatus() == PendingTransaction.TransactionStatus.INVOICED ||
            transaction.getStatus() == PendingTransaction.TransactionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot record payment for a " + transaction.getStatus() + " transaction");
        }

        BigDecimal newPaidAmount = transaction.getPaidAmount().add(amount);
        transaction.setPaidAmount(newPaidAmount);

        if (transaction.isFullyPaid()) {
            transaction.setStatus(PendingTransaction.TransactionStatus.INVOICED);
            transaction.setInvoicedAt(LocalDateTime.now());
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            transaction.setStatus(PendingTransaction.TransactionStatus.PARTIAL_PAID);
        }

        log.info("Recorded payment of {} for transaction {}. New status: {}", 
                amount, transactionId, transaction.getStatus());

        return transactionRepository.save(transaction);
    }

    /**
     * Apply a discount to a transaction.
     */
    public PendingTransaction applyDiscount(Long transactionId, BigDecimal discountAmount, String reason) {
        PendingTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        transaction.setDiscountAmount(discountAmount);
        if (reason != null) {
            String existingNotes = transaction.getNotes() != null ? transaction.getNotes() + "\n" : "";
            transaction.setNotes(existingNotes + "Discount: " + reason);
        }

        log.info("Applied discount of {} to transaction {}", discountAmount, transactionId);
        return transactionRepository.save(transaction);
    }

    /**
     * Convert pending transaction to an invoice.
     */
    public Invoice convertToInvoice(Long transactionId) {
        PendingTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (transaction.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create invoice for empty transaction");
        }

        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setOwner(transaction.getOwner());
        invoice.setDate(java.time.LocalDate.now());
        invoice.setInvoiceNumber("INV-" + java.util.UUID.randomUUID().toString().substring(0, 8));
        invoice.setAmount(transaction.getTotalAmount());
        invoice.setTotalAmount(transaction.getTotalAmount().subtract(transaction.getDiscountAmount()));
        invoice.setPaidAmount(transaction.getPaidAmount());
        invoice.setStatus(transaction.isFullyPaid() ? Invoice.Status.PAID : Invoice.Status.PENDING);
        invoice.setNotes(transaction.getNotes());

        // Create invoice items
        List<InvoiceItem> invoiceItems = transaction.getItems().stream()
                .map(item -> {
                    InvoiceItem invoiceItem = new InvoiceItem();
                    invoiceItem.setInvoice(invoice);
                    invoiceItem.setDescription(item.getDescription());
                    invoiceItem.setQuantity(item.getQuantity());
                    invoiceItem.setUnitPrice(item.getUnitPrice());
                    invoiceItem.setLineTotal(item.getLineTotal());
                    invoiceItem.setItemType(mapToInvoiceItemType(item.getServiceType()));
                    return invoiceItem;
                })
                .toList();

        invoice.setInvoiceItems(invoiceItems);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Update transaction
        transaction.setStatus(PendingTransaction.TransactionStatus.INVOICED);
        transaction.setInvoice(savedInvoice);
        transaction.setInvoicedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.info("Converted transaction {} to invoice {}", transactionId, savedInvoice.getInvoiceId());
        return savedInvoice;
    }

    /**
     * Get pending transactions for an owner.
     */
    @Transactional(readOnly = true)
    public List<PendingTransaction> getByOwner(Long ownerId) {
        return transactionRepository.findByOwnerOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    /**
     * Get pending transactions for an animal.
     */
    @Transactional(readOnly = true)
    public List<PendingTransaction> getByAnimal(Long animalId) {
        return transactionRepository.findByAnimalAnimalIdOrderByCreatedAtDesc(animalId);
    }

    /**
     * Get total pending amount for an owner.
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalPendingForOwner(Long ownerId) {
        return transactionRepository.getTotalPendingAmountByOwner(ownerId);
    }

    /**
     * Get a transaction by ID.
     */
    @Transactional(readOnly = true)
    public PendingTransaction getById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
    }

    /**
     * Cancel a pending transaction.
     */
    public PendingTransaction cancel(Long transactionId, String reason) {
        PendingTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (transaction.getStatus() == PendingTransaction.TransactionStatus.INVOICED) {
            throw new IllegalStateException("Cannot cancel an invoiced transaction");
        }

        transaction.setStatus(PendingTransaction.TransactionStatus.CANCELLED);
        String existingNotes = transaction.getNotes() != null ? transaction.getNotes() + "\n" : "";
        transaction.setNotes(existingNotes + "Cancelled: " + (reason != null ? reason : "No reason provided"));

        log.info("Cancelled transaction {}", transactionId);
        return transactionRepository.save(transaction);
    }

    // Helper methods
    private PendingTransactionItem.ServiceType mapServiceCategory(String category) {
        if (category == null) return PendingTransactionItem.ServiceType.OTHER;
        
        return switch (category.toUpperCase()) {
            case "EXAMINATION", "EXAM" -> PendingTransactionItem.ServiceType.EXAMINATION;
            case "VACCINATION", "VACCINE" -> PendingTransactionItem.ServiceType.VACCINATION;
            case "SURGERY" -> PendingTransactionItem.ServiceType.SURGERY;
            case "LAB", "LABORATORY" -> PendingTransactionItem.ServiceType.LAB_TEST;
            case "RADIOLOGY", "XRAY", "IMAGING" -> PendingTransactionItem.ServiceType.RADIOLOGY;
            case "HOSPITALIZATION" -> PendingTransactionItem.ServiceType.HOSPITALIZATION;
            case "MEDICATION", "MEDICINE" -> PendingTransactionItem.ServiceType.MEDICATION;
            case "GROOMING" -> PendingTransactionItem.ServiceType.GROOMING;
            case "CONSULTATION" -> PendingTransactionItem.ServiceType.CONSULTATION;
            case "EMERGENCY" -> PendingTransactionItem.ServiceType.EMERGENCY;
            case "FOLLOW_UP", "FOLLOWUP" -> PendingTransactionItem.ServiceType.FOLLOW_UP;
            default -> PendingTransactionItem.ServiceType.OTHER;
        };
    }

    private InvoiceItem.ItemType mapToInvoiceItemType(PendingTransactionItem.ServiceType serviceType) {
        return switch (serviceType) {
            case VACCINATION -> InvoiceItem.ItemType.VACCINE;
            case SURGERY -> InvoiceItem.ItemType.SURGERY;
            case MEDICATION -> InvoiceItem.ItemType.MEDICINE;
            case LAB_TEST -> InvoiceItem.ItemType.LAB_TEST;
            default -> InvoiceItem.ItemType.SERVICE;
        };
    }
}
