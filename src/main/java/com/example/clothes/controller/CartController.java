package com.example.clothes.controller;

import com.example.clothes.dto.CartDTO;
import com.example.clothes.dto.CartItemDTO;
import com.example.clothes.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping("/add")
    public ResponseEntity<CartDTO> createCart(@RequestBody() CartDTO cartDTO, HttpServletRequest request){
        return ResponseEntity.ok(cartService.createCart(cartDTO));
    }

    @GetMapping("/cart-detail")
    public ResponseEntity<CartDTO> findByUser(HttpServletRequest request){
        return ResponseEntity.ok(cartService.findByUser());
    }

    @PutMapping("/update-quantity")
    public ResponseEntity<CartItemDTO> updateQuantity(@RequestBody() CartItemDTO cartItemDTO){
        return ResponseEntity.ok(cartService.updateQuantity(cartItemDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable(name = "id") Long id){
        cartService.deleteCartItem(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/cart-items/{id}/increase")
    public ResponseEntity<Void> increaseQuantity(@PathVariable(name = "id") Long id){
        cartService.increasedQuantity(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cart-items/{id}/decrease")
    public ResponseEntity<Void> decreasedQuantity(@PathVariable(name = "id") Long id){
        cartService.decreasedQuantity(id);
        return ResponseEntity.ok().build();
    }
}
