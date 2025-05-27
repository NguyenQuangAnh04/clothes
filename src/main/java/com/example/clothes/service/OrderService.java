package com.example.clothes.service;

import com.example.clothes.dto.OrderDTO;
import com.example.clothes.dto.OrderItemDTO;
import com.example.clothes.enums.OrderStatus;
import com.example.clothes.model.*;
import com.example.clothes.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public Page<OrderDTO> findAll(PageRequest pageRequest) {
        return orderRepository.findAll(pageRequest).map(item -> {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(item.getOrderId());
            orderDTO.setPhone(item.getPhone());
            orderDTO.setEmail(item.getEmail());
            orderDTO.setCustomerName(item.getCustomerName());
            orderDTO.setCreate_at(item.getCreate_at());
            return orderDTO;
        });
    }

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Order order = new Order();
        order.setCustomerName(orderDTO.getCustomerName());
        order.setPhone(orderDTO.getPhone());
        order.setEmail(orderDTO.getEmail());
        order.setAddress(orderDTO.getAddress());
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setNote(orderDTO.getNote());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setOrderDate(LocalDateTime.now());
        Double totalMoney = orderDTO.getOrderItemDTOS().stream().map(OrderItemDTO::getTotalAmount).reduce(0.0, Double::sum);
        order.setTotalAmount(totalMoney);
        order.setUser(user);
        order = orderRepository.save(order);
        for (OrderItemDTO orderItemDTO : orderDTO.getOrderItemDTOS()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setSize(orderItemDTO.getSize());
            orderItem.setColor(orderItemDTO.getColor());
            Product orderProduct = productRepository.findById(orderItemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
            Inventory inventory = inventoryRepository.findByProductAndColorAndSize(orderProduct, orderItem.getColor(), orderItem.getSize())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
            if (inventory.getQuantity() < orderItemDTO.getQuantity()) {
                throw new RuntimeException("Số lượng tồn kho không đủ");
            }
            inventory.setQuantity(inventory.getQuantity() - orderItemDTO.getQuantity());
            orderItem.setProduct(orderProduct);
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setSubtotal(orderItemDTO.getTotalAmount());
            inventoryRepository.save(inventory);
            orderItemRepository.save(orderItem);
        }
        OrderDTO responseDTO = new OrderDTO();
        responseDTO.setOrderId(order.getOrderId());
        responseDTO.setCustomerName(order.getCustomerName());
        responseDTO.setCreate_at(order.getCreate_at());
        responseDTO.setEmail(order.getEmail());
        responseDTO.setPhone(order.getPhone());
        List<OrderItemDTO> orderItemDTOList = orderItemRepository.findByOrder(order).stream()
                .map(orderItem -> {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setId(orderItem.getOrderItemId());
                    orderItemDTO.setProductName(orderItem.getProduct().getProductName());
                    orderItemDTO.setQuantity(orderItem.getQuantity());
                    orderItemDTO.setProductId(orderItem.getProduct().getId());
                    orderItemDTO.setSize(orderItem.getSize());
                    orderItemDTO.setPrice(orderItem.getProduct().getPrice());
                    orderItemDTO.setColor(orderItem.getColor());
                    orderItemDTO.setTotalAmount(orderItem.getSubtotal());
                    return orderItemDTO;
                }).collect(Collectors.toList());
        responseDTO.setOrderItemDTOS(orderItemDTOList);
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        user.setCart(null);
        cartRepository.delete(cart);
        return responseDTO;
    }

    @Override
    public List<OrderDTO> findOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        List<Order> orders = orderRepository.findAllUser(user);
        if (orders.isEmpty()) {
            throw new RuntimeException("Không có đơn hàng nào");
        }
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Order order : orders) {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setCustomerName(order.getCustomerName());
            dto.setEmail(order.getEmail());
            dto.setSubtotal(order.getTotalAmount());
            dto.setPhone(order.getPhone());
            dto.setAddress(order.getAddress());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setCreate_at(order.getOrderDate());
            dto.setStatus(order.getOrderStatus().name());
            dto.setNote(order.getNote());
            // Không lấy OrderItem ở đây!
            orderDTOList.add(dto);
        }
        return orderDTOList;
    }

    @Override
    public OrderDTO findOrderDetail(Long userId, Long orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Order order = orderRepository.findByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerName(order.getCustomerName());
        dto.setEmail(order.getEmail());
        dto.setSubtotal(order.getTotalAmount());
        dto.setPhone(order.getPhone());
        dto.setAddress(order.getAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreate_at(order.getOrderDate());
        dto.setStatus(order.getOrderStatus().name());
        dto.setNote(order.getNote());
        List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setId(item.getOrderItemId());
            Inventory inventory = inventoryRepository.findByProductAndColorAndSize(item.getProduct(), item.getColor(), item.getSize())
                    .orElseThrow(() ->  new EntityNotFoundException("Không tìm thấy product"));
            orderItemDTO.setImage_url(inventory.getImage_url());
            orderItemDTO.setProductName(item.getProduct().getProductName());
            orderItemDTO.setQuantity(item.getQuantity());
            orderItemDTO.setProductId(item.getProduct().getId());
            orderItemDTO.setSize(item.getSize());
            orderItemDTO.setPrice(item.getProduct().getPrice());
            orderItemDTO.setColor(item.getColor());
            orderItemDTO.setTotalAmount(item.getSubtotal());
            orderItemDTOList.add(orderItemDTO);
        }
        dto.setOrderItemDTOS(orderItemDTOList);
        return dto;
    }

    @Override
    public void cancelled(Long userId, Long orderId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
        Optional<Order> order = orderRepository.findByOrderIdAndUser(orderId, user);
        if(!order.isPresent()) throw new EntityNotFoundException("Không tìm thấy đơn hàng");
        order.get().setOrderStatus(OrderStatus.CANCELLED);
        order.get().setOrderDate(LocalDateTime.now());
        orderRepository.save(order.get());
    }
}
