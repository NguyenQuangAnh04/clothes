package com.example.clothes.service;

import com.example.clothes.enums.PaymentMethod;
import com.example.clothes.enums.PaymentStatus;
import com.example.clothes.model.Order;
import com.example.clothes.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class QRService implements IQRService {
    @Autowired
    private OrderRepository orderRepository;
    @Value("${account.number}")
    String accountNumber;
    @Override
    public String generateQR(Long orderId) {
        if (orderId == null) throw new IllegalArgumentException("OrderId cannot be null");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        validatePaymentMethod(order);
        String qrUrl = buildQRUrl(order);
        updatePaymentStatus(order);
        log.info("QR code generated successfully for order: {}", orderId);
        return qrUrl;
    }

    private void validatePaymentMethod(Order order) {
        if (!PaymentMethod.BANK.equals(order.getPaymentMethod())) {
            throw new EntityNotFoundException("QR code generation is only supported for bank payment method. Current method: " + order.getPaymentMethod());
        }
    }

    private String buildQRUrl(Order order) {
        try {
            String baseUrl = String.format("https://img.vietqr.io/image/MB-%s-compact.png", accountNumber);
            String codeOrder = generateOrderCode(order.getOrderId());
            String addInfo = "Thanh toan " + codeOrder;
            List<String> params = Arrays.asList(
                    "accountName=" + URLEncoder.encode(order.getCustomerName(), StandardCharsets.UTF_8),
                    "amount=" + order.getTotalAmount(),
                    "addInfo=" + URLEncoder.encode(addInfo, StandardCharsets.UTF_8)
            );
            return baseUrl + "?" + String.join("&", params);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR URL for order: " + order.getOrderId(), e);
        }
    }

    private String generateOrderCode(Long orderId) {
        return String.format("DH%03d", orderId);
    }


    private void updatePaymentStatus(Order order) {
        try {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            orderRepository.save(order);
            log.info("Payment status updated to COMPLETED for order: {}", order.getOrderId());
        } catch (Exception e) {
            log.error("Failed to update payment status for order: {}", order.getOrderId(), e);
            throw new RuntimeException("Failed to update payment status", e);
        }
    }
}
