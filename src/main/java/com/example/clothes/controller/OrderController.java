package com.example.clothes.controller;

import com.example.clothes.dto.OrderDTO;
import com.example.clothes.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/add-order")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody() OrderDTO orderDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(orderService.createOrder(orderDTO, userId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getOrderUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(orderService.findOrders(userId));
    }

    @GetMapping("/order-details/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetail(HttpServletRequest request, @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(orderService.findOrderDetail(userId, orderId));
    }

    @PutMapping("/cancelled/{orderId}")
    public ResponseEntity cancelled(HttpServletRequest request, @PathVariable(name = "orderId") Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelled(userId, orderId);
        return ResponseEntity.ok("Hủy thành công đơn hàng!");
    }

    @GetMapping("/order/{id}/pdf")
    public ResponseEntity<Resource> exportPdf(@PathVariable(name = "id") Long id) {
        ByteArrayInputStream input = orderService.exportInvoicePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(input));
    }
}
