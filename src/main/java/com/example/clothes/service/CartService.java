package com.example.clothes.service;

import com.example.clothes.dto.CartDTO;
import com.example.clothes.dto.CartItemDTO;
import com.example.clothes.model.*;
import com.example.clothes.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private InventoryRepository inventoryRepository;

    @Override
    public CartDTO createCart(CartDTO cartDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setCreatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });
        for (CartItemDTO itemDTO : cartDTO.getCartItemDTOList()) {
            Product product = productRepository.findById(itemDTO.getProductId()).orElseThrow(
                    () -> new EntityNotFoundException("Không tìm thấy sản phẩm!")
            );
            Optional<CartItem> existCart = cartItemRepository.findByCartAndProductAndSizeAndColor(cart, product, itemDTO.getSize(), itemDTO.getColor());
            if (existCart.isPresent()) {
                CartItem existCartItem = existCart.get();
                Integer newQuantity = itemDTO.getQuantity() + existCartItem.getQuantity();
                existCartItem.setQuantity(newQuantity);
                existCartItem.setSubtotal(newQuantity * product.getPrice());
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
                cartItem.setSubtotal(itemDTO.getQuantity() * product.getPrice());
                cartItemRepository.save(cartItem);
            }
        }
        return cartDTO;
    }

    @Transactional
    @Override
    public CartItemDTO updateQuantity(CartItemDTO cartItemDTO) {
        CartItem cartItem = cartItemRepository.findById(cartItemDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giỏ hàng!"));
        Product product = productRepository.findById(cartItemDTO.getProductId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        Inventory inventory = inventoryRepository.findByProductAndColorAndSize(product, cartItem.getColor(), cartItemDTO.getSize()).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong inventory"));
        if (inventory.getQuantity() < cartItemDTO.getQuantity()) throw new EntityNotFoundException("Không đủ hàng");
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setSubtotal(cartItem.getQuantity() * cartItem.getProduct().getPrice());
        cartItemRepository.save(cartItem);
        CartItemDTO result = new CartItemDTO();
        result.setId(cartItem.getId());
        result.setMaxQuantity(inventory.getQuantity());
        result.setProductName(cartItem.getProduct().getProductName());
        result.setQuantity(cartItem.getQuantity());
        result.setTotalMoney(cartItem.getSubtotal());
        return result;
    }

    @Override
    public CartDTO findByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) throw new EntityNotFoundException("Không tìm thấy giỏ hàng!");
        Optional<Cart> cart = cartRepository.findByUser(user.get());
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId(user.get().getUserId());
        List<CartItem> cartItem = cartItemRepository.findByCart(cart.get());
        List<CartItemDTO> result = new ArrayList<>();
        for (CartItem item : cartItem) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            Optional<Inventory> inventory = inventoryRepository.findByProductAndColorAndSize(item.getProduct(), item.getColor(), item.getSize());
            cartItemDTO.setId(item.getId());
            cartItemDTO.setProductId(item.getProduct().getId());
            cartItemDTO.setMaxQuantity(inventory.get().getQuantity());
            cartItemDTO.setQuantity(item.getQuantity());
            cartItemDTO.setColor(item.getColor());
            cartItemDTO.setSize(item.getSize());
            cartItemDTO.setPrice(item.getPrice());
            cartItemDTO.setTotalMoney(item.getSubtotal());
            cartItemDTO.setProductName(item.getProduct().getProductName());
            cartItemDTO.setImage_url(item.getProduct().getImage_url());
            result.add(cartItemDTO);
        }
        cartDTO.setId(cart.get().getId());
        cartDTO.setCartItemDTOList(result);
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
}
