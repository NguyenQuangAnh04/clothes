package com.example.clothes.controller;

import com.example.clothes.dto.OrderDTO;
import com.example.clothes.enums.OrderStatus;
import com.example.clothes.model.Order;
import com.example.clothes.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(name = "page", defaultValue = "0") int page,
                                                       @RequestParam(name = "limit", defaultValue = "5") int limit,
                                                       @RequestParam(name = "status", required = false) OrderStatus status,
                                                       @RequestParam(name = "orderId", required = false) Long orderId,
                                                       @RequestParam(name = "fullName", required = false) String fullName,
                                                       @RequestParam(name = "phone", required = false) String phone) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("orderId").descending());
        Page<OrderDTO> orderDTOPage = orderService.findAll(status, fullName, orderId,phone, pageRequest);
        Map<String, Object> map = new HashMap<>();
        Map<String, Long> counts = orderService.countOrdersByStatus();
        map.put("statusCounts", counts);
        map.put("orders", orderDTOPage.getContent());
        map.put("totalPages", orderDTOPage.getTotalPages());
        map.put("totalItems", orderDTOPage.getTotalElements());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/add-order")
    public ResponseEntity<Order> createOrder(@RequestBody() OrderDTO orderDTO, HttpServletRequest request) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getOrderUser(HttpServletRequest request) {
        return ResponseEntity.ok(orderService.findOrders());
    }

    @GetMapping("/order-details/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetail(HttpServletRequest request, @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(orderService.findOrderDetail(userId, orderId));
    }

    @PutMapping("/cancelled/{orderId}")
    public ResponseEntity<Void> cancelled(HttpServletRequest request, @PathVariable(name = "orderId") Long orderId) {
        orderService.cancelled(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{id}/pdf")
    public ResponseEntity<Resource> exportPdf(@PathVariable(name = "id") Long id) {
        ByteArrayInputStream input = orderService.exportInvoicePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(input));
    }

    @PutMapping("/editStatus/{id}")
    public ResponseEntity<Void> editStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") OrderStatus status) {
        orderService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable(name = "id") Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
