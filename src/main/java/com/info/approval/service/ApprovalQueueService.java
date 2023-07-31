package com.info.approval.service;

import com.info.approval.model.ApprovalQueue;
import com.info.approval.repository.ApprovalQueueRepository;
import com.info.product.model.Product;
import com.info.product.model.ProductStatus;
import com.info.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApprovalQueueService {
    private final ApprovalQueueRepository approvalQueueRepository;
    private final ProductRepository productRepository;

    public List<Product> getProductsInApprovalQueue() {
        List<ApprovalQueue> approvalQueueList = approvalQueueRepository.findAllByOrderByRequestDateAsc();

        return approvalQueueList.stream()
                .map(ApprovalQueue::getProduct)
                .collect(Collectors.toList());
    }
    public void approveProduct(Long approvalId) {
        ApprovalQueue approvalQueue = getApprovalQueueById(approvalId);
        Product product = approvalQueue.getProduct();
        product.setActive(ProductStatus.APPROVED);
        productRepository.save(product);
        approvalQueueRepository.delete(approvalQueue);
    }

    public void rejectProduct(Long approvalId) {
        ApprovalQueue approvalQueue = getApprovalQueueById(approvalId);
        approvalQueueRepository.delete(approvalQueue);
    }
    private ApprovalQueue getApprovalQueueById(Long approvalId) {
        return approvalQueueRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("Approval with ID " + approvalId + " not found"));
    }
}
