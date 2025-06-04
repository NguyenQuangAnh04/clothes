package com.example.clothes.controller;

import com.example.clothes.service.QRService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class QRController {
    private final QRService qrService;
    @GetMapping("/qr/{orderId}")
    public ResponseEntity<String> getVietQRUrl(@PathVariable(name = "orderId") Long orderId){
        return ResponseEntity.ok(qrService.generateQR(orderId));
    }
}
