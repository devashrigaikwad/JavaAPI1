package com.info.approval.controller;

import com.info.approval.model.ApprovalQueue;
import com.info.approval.service.ApprovalQueueService;
import com.info.product.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/approval-queue")
@AllArgsConstructor
public class ApprovalQueueController {
    private final ApprovalQueueService approvalQueueService;

    @GetMapping
    public List<Product> getApprovalQueue() {
        return approvalQueueService.getProductsInApprovalQueue();
    }

    @PutMapping("/{approvalId}/approve")
    public ResponseEntity<String> approveProduct(@PathVariable Long approvalId) {
        try {
            approvalQueueService.approveProduct(approvalId);
            return ResponseEntity.ok(approvalId +"--Approved");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{approvalId}/reject")
    public ResponseEntity<Void> rejectProduct(@PathVariable Long approvalId) {
        try {
            approvalQueueService.rejectProduct(approvalId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
