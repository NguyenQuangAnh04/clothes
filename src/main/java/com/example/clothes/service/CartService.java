package com.example.clothes.service;

import com.example.clothes.dto.CartDTO;
import com.example.clothes.dto.CartItemDTO;
import com.example.clothes.mapper.CartMapperDTO;
import com.example.clothes.model.*;
import com.example.clothes.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICartService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private CartMapperDTO cartMapperDTO;
    @Override
    public CartDTO createCart(CartDTO cartDTO) {
        User user = userRepository.findById(currentUserService.getCurrentUser().getUserId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy tài khoản"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCreatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });
        for (CartItemDTO itemDTO : cartDTO.getCarts()) {
            Product product = productRepository.findById(itemDTO.getProductId()).orElseThrow(
                    () -> new EntityNotFoundException("Không tìm thấy sản phẩm!")
            );
            Optional<CartItem> existCart = cartItemRepository
                    .findByCartAndProductAndSizeAndColor(cart, product, itemDTO.getSize(), itemDTO.getColor());
            if (existCart.isPresent()) {
                CartItem existCartItem = existCart.get();
                Integer newQuantity = itemDTO.getQuantity() + existCartItem.getQuantity();
                existCartItem.setQuantity(newQuantity);
                existCartItem.setSubtotal(BigDecimal.valueOf(newQuantity * product.getPrice()));
                cartItemRepository.save(existCartItem);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(product);
                if(itemDTO.getSize() == null){
                    throw new EntityNotFoundException("Chưa chọn size");
                }
                cartItem.setSize(itemDTO.getSize());
                if(itemDTO.getColor() == null){
                    throw new EntityNotFoundException("Chưa chọn màu");
                }
                cartItem.setColor(itemDTO.getColor());
                cartItem.setQuantity(itemDTO.getQuantity());
                cartItem.setCart(cart);
                cartItem.setPrice(itemDTO.getPrice());
                cartItem.setSubtotal(BigDecimal.valueOf(itemDTO.getQuantity() * product.getPrice()));
                cartItemRepository.save(cartItem);
            }
        }
        return cartDTO;
    }

    @Transactional
    @Override
    public CartItemDTO updateQuantity(CartItemDTO cartItemDTO) {
//        CartItem cartItem = cartItemRepository.findById(cartItemDTO.getId())
//                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giỏ hàng!"));
//        Product product = productRepository.findById(cartItemDTO.getProductId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
//        Variant variant = variantRepository.findByProductAndColorAndSize(product, cartItem.getColor(), cartItemDTO.getSize()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong inventory"));
//        if (variant.getQuantity() < cartItemDTO.getQuantity()) throw new EntityNotFoundException("Không đủ hàng");
//        cartItem.setQuantity(cartItemDTO.getQuantity());
//        cartItem.setSubtotal(cartItem.getQuantity() * cartItem.getProduct().getPrice());
//        cartItemRepository.save(cartItem);
//        CartItemDTO result = new CartItemDTO();
//        result.setId(cartItem.getId());
//        result.setMaxQuantity(variant.getQuantity());
//        result.setProductName(cartItem.getProduct().getProductName());
//        result.setQuantity(cartItem.getQuantity());
//        result.setTotalMoney(cartItem.getSubtotal());
        return null;
    }

    @Override
    public CartDTO findByUser() {
        User user = userRepository.findById(currentUserService.getCurrentUser().getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng!"));

        Optional<Cart> cartOpt = cartRepository.findByUser(user);

        if (cartOpt.isEmpty() || !cartItemRepository.existsByCart(cartOpt.get())) {
            CartDTO emptyCart = new CartDTO();
            emptyCart.setId(null);
            emptyCart.setCarts(new ArrayList<>());
            emptyCart.setTotalMoney(BigDecimal.ZERO);
            return emptyCart;
        }
        Cart cart = cartOpt.get();
        List<CartItem> cartItem = cartItemRepository.findByCart(cart);
        BigDecimal totalPrice = cartItemRepository.sumPricesByCart(cart.getId());
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setTotalMoney(totalPrice);
        List<CartItemDTO> result = new ArrayList<>();
        for (CartItem item : cartItem) {
            result.add(cartMapperDTO.mapCartItemToDTO(item));
        }
        cartDTO.setCarts(result);
        return cartDTO;
    }

    @Transactional
    @Override
    public void deleteCartItem(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Không tìm thấy gio hàng"));
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        if(!cartItemRepository.existsByCart(cart)){
            cartRepository.deleteById(cart.getId());
        }
    }
    @Override
    public void increasedQuantity(Long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(() -> new RuntimeException("not find id"));

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void decreasedQuantity(Long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(() -> new RuntimeException("not find id"));
        cartItem.setQuantity(cartItem.getQuantity() - 1);

        cartItemRepository.save(cartItem);
    }
}
