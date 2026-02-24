package com.hss.hss_backend.controller;

import com.hss.hss_backend.entity.Invoice;
import com.hss.hss_backend.entity.PendingTransaction;
import com.hss.hss_backend.entity.PendingTransactionItem;
import com.hss.hss_backend.service.PendingTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pending-transactions")
@RequiredArgsConstructor
@Tag(name = "Pending Transactions", description = "Cart/pending balance management APIs")
public class PendingTransactionController {

    private final PendingTransactionService transactionService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a pending transaction by ID")
    public ResponseEntity<PendingTransaction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get all pending transactions for an owner")
    public ResponseEntity<List<PendingTransaction>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(transactionService.getByOwner(ownerId));
    }

    @GetMapping("/animal/{animalId}")
    @Operation(summary = "Get all pending transactions for an animal")
    public ResponseEntity<List<PendingTransaction>> getByAnimal(@PathVariable Long animalId) {
        return ResponseEntity.ok(transactionService.getByAnimal(animalId));
    }

    @GetMapping("/owner/{ownerId}/total")
    @Operation(summary = "Get total pending amount for an owner")
    public ResponseEntity<Map<String, BigDecimal>> getTotalPendingForOwner(@PathVariable Long ownerId) {
        BigDecimal total = transactionService.getTotalPendingForOwner(ownerId);
        return ResponseEntity.ok(Map.of("totalPending", total));
    }

    @PostMapping("/owner/{ownerId}/create")
    @Operation(summary = "Create or get existing pending transaction for an owner")
    public ResponseEntity<PendingTransaction> createForOwner(
            @PathVariable Long ownerId,
            @RequestParam Long clinicId) {
        return ResponseEntity.ok(transactionService.getOrCreateForOwner(ownerId, clinicId));
    }

    @PostMapping("/animal/{animalId}/create")
    @Operation(summary = "Create or get existing pending transaction for an animal")
    public ResponseEntity<PendingTransaction> createForAnimal(
            @PathVariable Long animalId,
            @RequestParam Long clinicId) {
        return ResponseEntity.ok(transactionService.getOrCreateForAnimal(animalId, clinicId));
    }

    @PostMapping("/{transactionId}/items")
    @Operation(summary = "Add an item to a pending transaction")
    public ResponseEntity<PendingTransactionItem> addItem(
            @PathVariable Long transactionId,
            @RequestBody AddItemRequest request) {
        PendingTransactionItem item = transactionService.addItem(
                transactionId,
                request.serviceType(),
                request.serviceId(),
                request.description(),
                request.unitPrice(),
                request.quantity(),
                request.notes()
        );
        return ResponseEntity.ok(item);
    }

    @PostMapping("/{transactionId}/items/service/{serviceId}")
    @Operation(summary = "Add a VetService item to a pending transaction")
    public ResponseEntity<PendingTransactionItem> addServiceItem(
            @PathVariable Long transactionId,
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(transactionService.addServiceItem(transactionId, serviceId, quantity, notes));
    }

    @DeleteMapping("/{transactionId}/items/{itemId}")
    @Operation(summary = "Remove an item from a pending transaction")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long transactionId,
            @PathVariable Long itemId) {
        transactionService.removeItem(transactionId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{transactionId}/payment")
    @Operation(summary = "Record a payment for a pending transaction")
    public ResponseEntity<PendingTransaction> recordPayment(
            @PathVariable Long transactionId,
            @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(transactionService.recordPayment(
                transactionId,
                request.amount(),
                request.paymentMethod(),
                request.notes()
        ));
    }

    @PostMapping("/{transactionId}/discount")
    @Operation(summary = "Apply a discount to a pending transaction")
    public ResponseEntity<PendingTransaction> applyDiscount(
            @PathVariable Long transactionId,
            @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(transactionService.applyDiscount(
                transactionId,
                request.amount(),
                request.reason()
        ));
    }

    @PostMapping("/{transactionId}/convert-to-invoice")
    @Operation(summary = "Convert a pending transaction to an invoice")
    public ResponseEntity<Invoice> convertToInvoice(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.convertToInvoice(transactionId));
    }

    @PostMapping("/{transactionId}/cancel")
    @Operation(summary = "Cancel a pending transaction")
    public ResponseEntity<PendingTransaction> cancel(
            @PathVariable Long transactionId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(transactionService.cancel(transactionId, reason));
    }

    // Request records
    public record AddItemRequest(
            PendingTransactionItem.ServiceType serviceType,
            Long serviceId,
            String description,
            BigDecimal unitPrice,
            Integer quantity,
            String notes
    ) {}

    public record PaymentRequest(
            BigDecimal amount,
            String paymentMethod,
            String notes
    ) {}

    public record DiscountRequest(
            BigDecimal amount,
            String reason
    ) {}
}
